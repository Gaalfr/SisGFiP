package com.sisgfip.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sisgfip.model.Movimentacao.TipoMovimentacao;
import com.sisgfip.model.Usuario.Role;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTOs (Data Transfer Objects) do SisGFiP.
 *
 * Separar DTOs dos Models é uma boa prática:
 *   - Evita expor campos sensíveis (senha, id interno)
 *   - Permite versionar a API sem mudar o banco
 *   - Controla exatamente o que entra e sai da API
 *
 * Usando Java Records (Java 16+) — imutáveis e sem boilerplate.
 */
public final class DTOs {

    private DTOs() {} // classe utilitária, não instanciar

    // ╔══════════════════════════════════════╗
    // ║   USUARIO                            ║
    // ╚══════════════════════════════════════╝

    /** Payload para criar ou atualizar usuário */
    public record UsuarioRequest(
        @NotBlank(message = "Nome de usuário é obrigatório")
        @Size(min = 3, max = 50)
        String nomeUsuario,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha
    ) {}

    /** Resposta ao cliente — SEM a senha */
    public record UsuarioResponse(
        Long id,
        String nomeUsuario,
        Role role,
        boolean ativo,
        LocalDateTime criadoEm
    ) {}

    // ╔══════════════════════════════════════╗
    // ║   AUTENTICAÇÃO / LOGIN               ║
    // ╚══════════════════════════════════════╝

    public record LoginRequest(
        @NotBlank String nomeUsuario,
        @NotBlank String senha
    ) {}

    /** Resposta do login — inclui token JWT (preenchido quando JWT for ativado) */
    public record LoginResponse(
        Long usuarioId,
        String nomeUsuario,
        String token,        // JWT — por enquanto retorna null
        String tipo          // "Bearer"
    ) {}

    // ╔══════════════════════════════════════╗
    // ║   CONTA                              ║
    // ╚══════════════════════════════════════╝

    public record ContaRequest(
        @NotBlank(message = "Nome da conta é obrigatório")
        @Size(max = 100)
        String nome,

        @NotNull(message = "ID do usuário é obrigatório")
        Long usuarioId
    ) {}

    public record ContaResponse(
        Long id,
        String nome,
        BigDecimal saldo,
        Long usuarioId,
        String nomeUsuario,
        boolean ativa,
        LocalDateTime criadaEm
    ) {}

    /** Resumo financeiro da conta (dashboard) */
    public record ContaResumo(
        Long contaId,
        String nomeConta,
        BigDecimal saldo,
        BigDecimal totalReceitas,
        BigDecimal totalDespesas
    ) {}

    // ╔══════════════════════════════════════╗
    // ║   MOVIMENTAÇÃO                       ║
    // ╚══════════════════════════════════════╝

    public record MovimentacaoRequest(
        @NotNull(message = "Tipo é obrigatório (RECEITA ou DESPESA)")
        TipoMovimentacao tipo,

        @NotNull
        @Positive(message = "Valor deve ser positivo")
        BigDecimal valor,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 200)
        String descricao,

        @Size(max = 50)
        String categoria,

        @NotNull(message = "ID da conta é obrigatório")
        Long contaId
    ) {}

    public record MovimentacaoResponse(
        Long id,
        TipoMovimentacao tipo,
        BigDecimal valor,
        String descricao,
        String categoria,
        LocalDateTime dataHora,
        Long contaId,
        String nomeConta
    ) {}

    // ╔══════════════════════════════════════╗
    // ║   RESPOSTA PADRÃO DE ERRO            ║
    // ╚══════════════════════════════════════╝

    /**
     * Envelope de erro retornado em todos os erros da API.
     * Facilita o tratamento no cliente (Swing ou outro).
     */
    public record ErrorResponse(
        int status,
        String erro,
        String mensagem,
        LocalDateTime timestamp
    ) {}
}
