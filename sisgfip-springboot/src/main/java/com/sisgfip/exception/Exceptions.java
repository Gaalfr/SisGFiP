package com.sisgfip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceções de domínio do SisGFiP.
 *
 * Manter exceções no pacote `exception` permite que os Services
 * lancem erros semânticos sem depender do HTTP — o GlobalExceptionHandler
 * faz o mapeamento para status HTTP.
 */
public final class Exceptions {

    private Exceptions() {}

    // ── Recurso não encontrado (404) ──────────────────────────────

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String mensagem) {
            super(mensagem);
        }

        public static ResourceNotFoundException usuario(Long id) {
            return new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }

        public static ResourceNotFoundException conta(Long id) {
            return new ResourceNotFoundException("Conta não encontrada com id: " + id);
        }

        public static ResourceNotFoundException movimentacao(Long id) {
            return new ResourceNotFoundException("Movimentação não encontrada com id: " + id);
        }
    }

    // ── Conflito de dados (409) ───────────────────────────────────

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ConflictException extends RuntimeException {
        public ConflictException(String mensagem) {
            super(mensagem);
        }
    }

    // ── Credenciais inválidas (401) ───────────────────────────────

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String mensagem) {
            super(mensagem);
        }
    }

    // ── Regra de negócio violada (422) ───────────────────────────

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public static class BusinessException extends RuntimeException {
        public BusinessException(String mensagem) {
            super(mensagem);
        }
    }
}
