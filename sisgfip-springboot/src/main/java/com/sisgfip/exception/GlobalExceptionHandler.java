package com.sisgfip.exception;

import com.sisgfip.dto.DTOs.ErrorResponse;
import com.sisgfip.exception.Exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Intercepta todas as exceções lançadas pelos Controllers e Services,
 * convertendo-as em respostas JSON padronizadas.
 *
 * O cliente Swing (ou qualquer outro) sempre recebe o mesmo envelope:
 * { status, erro, mensagem, timestamp }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 — Recurso não encontrado */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Não encontrado", ex.getMessage());
    }

    /** 409 — Conflito (ex: usuário duplicado) */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return build(HttpStatus.CONFLICT, "Conflito", ex.getMessage());
    }

    /** 401 — Não autorizado */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Não autorizado", ex.getMessage());
    }

    /** 422 — Regra de negócio */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Erro de negócio", ex.getMessage());
    }

    /**
     * 400 — Erros de validação (@Valid)
     * Concatena todos os erros de campo em uma mensagem legível.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String detalhes = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "Erro de validação", detalhes);
    }

    /** 500 — Qualquer outra exceção não tratada */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
            "Ocorreu um erro inesperado: " + ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String erro, String mensagem) {
        var body = new ErrorResponse(status.value(), erro, mensagem, LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
