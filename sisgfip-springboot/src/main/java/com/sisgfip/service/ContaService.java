package com.sisgfip.service;

import com.sisgfip.dto.DTOs.*;
import com.sisgfip.exception.Exceptions.*;
import com.sisgfip.model.Conta;
import com.sisgfip.model.Usuario;
import com.sisgfip.repository.ContaRepository;
import com.sisgfip.repository.MovimentacaoRepository;
import com.sisgfip.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço de negócio para Contas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContaService {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    @Transactional
    public ContaResponse criar(ContaRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
            .orElseThrow(() -> ResourceNotFoundException.usuario(request.usuarioId()));

        Conta conta = Conta.builder()
            .nome(request.nome())
            .usuario(usuario)
            .build();

        conta = contaRepository.save(conta);
        log.info("Conta criada: id={}, nome={}, usuário={}", conta.getId(), conta.getNome(), usuario.getNomeUsuario());
        return toResponse(conta);
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ContaResponse> listarPorUsuario(Long usuarioId) {
        return contaRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public ContaResponse atualizar(Long id, ContaRequest request) {
        Conta conta = findOrThrow(id);
        conta.setNome(request.nome());
        return toResponse(contaRepository.save(conta));
    }

    @Transactional
    public void desativar(Long id) {
        Conta conta = findOrThrow(id);
        conta.setAtiva(false);
        contaRepository.save(conta);
        log.info("Conta desativada: id={}", id);
    }

    /** Retorna o resumo financeiro de uma conta (saldo + totais) */
    @Transactional(readOnly = true)
    public ContaResumo buscarResumo(Long contaId) {
        Conta conta = findOrThrow(contaId);
        BigDecimal receitas = movimentacaoRepository.sumReceitasByContaId(contaId);
        BigDecimal despesas = movimentacaoRepository.sumDespesasByContaId(contaId);
        return new ContaResumo(
            conta.getId(),
            conta.getNome(),
            conta.getSaldo(),
            receitas,
            despesas
        );
    }

    private Conta findOrThrow(Long id) {
        return contaRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.conta(id));
    }

    public ContaResponse toResponse(Conta c) {
        return new ContaResponse(
            c.getId(),
            c.getNome(),
            c.getSaldo(),
            c.getUsuario().getId(),
            c.getUsuario().getNomeUsuario(),
            c.isAtiva(),
            c.getCriadaEm()
        );
    }
}
