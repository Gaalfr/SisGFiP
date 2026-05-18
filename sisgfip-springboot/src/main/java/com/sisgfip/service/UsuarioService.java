package com.sisgfip.service;

import com.sisgfip.dto.DTOs.*;
import com.sisgfip.exception.Exceptions.*;
import com.sisgfip.model.Conta;
import com.sisgfip.model.Usuario;
import com.sisgfip.repository.ContaRepository;
import com.sisgfip.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável pela lógica de negócio dos Usuários.
 *
 * Controllers → Service → Repository (nunca pule camadas).
 * A camada Service é o lugar certo para:
 *   - Validações de negócio
 *   - Criptografia de senha
 *   - Orquestração entre repositórios
 *   - Transações (@Transactional)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    // ── CRUD ──────────────────────────────────────────────────────

    /** Cria um novo usuário e uma conta padrão para ele */
    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        if (usuarioRepository.existsByNomeUsuario(request.nomeUsuario())) {
            throw new ConflictException(
                "Já existe um usuário com o nome: " + request.nomeUsuario());
        }

        // Hash da senha — NUNCA persista senha em texto puro
        String senhaHash = passwordEncoder.encode(request.senha());

        Usuario usuario = Usuario.builder()
            .nomeUsuario(request.nomeUsuario())
            .senha(senhaHash)
            .build();

        usuario = usuarioRepository.save(usuario);
        log.info("Usuário criado: id={}, nome={}", usuario.getId(), usuario.getNomeUsuario());

        // Cria automaticamente uma conta padrão para o novo usuário
        Conta contaPadrao = Conta.builder()
            .nome("Conta Principal")
            .usuario(usuario)
            .build();
        contaRepository.save(contaPadrao);

        return toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAllAtivos()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    /** Atualiza apenas a senha (sem alterar nome) */
    @Transactional
    public UsuarioResponse atualizarSenha(Long id, String novaSenha) {
        Usuario usuario = findOrThrow(id);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        return toResponse(usuarioRepository.save(usuario));
    }

    /** Soft delete — não remove do banco, apenas desativa */
    @Transactional
    public void desativar(Long id) {
        Usuario usuario = findOrThrow(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuário desativado: id={}", id);
    }

    // ── Login ────────────────────────────────────────────────────

    /**
     * Valida credenciais e retorna dados do usuário.
     * O token JWT será gerado aqui quando implementado.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByNomeUsuario(request.nomeUsuario())
            .orElseThrow(() -> new UnauthorizedException("Usuário ou senha incorretos"));

        if (!usuario.isAtivo()) {
            throw new UnauthorizedException("Conta desativada");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new UnauthorizedException("Usuário ou senha incorretos");
        }

        log.info("Login realizado: usuário={}", usuario.getNomeUsuario());

        // TODO: gerar JWT aqui e retornar no token
        return new LoginResponse(
            usuario.getId(),
            usuario.getNomeUsuario(),
            null,    // token JWT — implementar com JwtService
            "Bearer"
        );
    }

    // ── Utilitários ───────────────────────────────────────────────

    private Usuario findOrThrow(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> Exceptions.ResourceNotFoundException.usuario(id));
    }

    /** Converte entidade → DTO de resposta */
    public UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
            u.getId(),
            u.getNomeUsuario(),
            u.getRole(),
            u.isAtivo(),
            u.getCriadoEm()
        );
    }
}
