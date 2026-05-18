package com.sisgfip.controller;

import com.sisgfip.dto.DTOs.*;
import com.sisgfip.model.Movimentacao.TipoMovimentacao;
import com.sisgfip.service.MovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller REST para Movimentações.
 * Base URL: /api/movimentacoes
 *
 * Suporta paginação nativa do Spring Data — o cliente pode passar:
 *   ?page=0&size=20&sort=dataHora,desc
 */
@RestController
@RequestMapping("/movimentacoes")
@RequiredArgsConstructor
@Tag(name = "Movimentações", description = "Registro e consulta de receitas e despesas")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    /** POST /api/movimentacoes — registra receita ou despesa */
    @PostMapping
    @Operation(
        summary = "Registrar movimentação",
        description = "Registra uma RECEITA ou DESPESA e atualiza o saldo da conta automaticamente"
    )
    public ResponseEntity<MovimentacaoResponse> registrar(
            @RequestBody @Valid MovimentacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(movimentacaoService.registrar(request));
    }

    /** GET /api/movimentacoes/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar movimentação por ID")
    public ResponseEntity<MovimentacaoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoService.buscarPorId(id));
    }

    /**
     * GET /api/movimentacoes/conta/{contaId}
     * Extrato paginado — ex: ?page=0&size=10
     */
    @GetMapping("/conta/{contaId}")
    @Operation(summary = "Extrato paginado da conta")
    public ResponseEntity<Page<MovimentacaoResponse>> extrato(
            @PathVariable Long contaId,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {
        return ResponseEntity.ok(movimentacaoService.listarPorConta(contaId, pageable));
    }

    /**
     * GET /api/movimentacoes/conta/{contaId}/tipo?tipo=RECEITA
     * Filtra por RECEITA ou DESPESA
     */
    @GetMapping("/conta/{contaId}/tipo")
    @Operation(summary = "Filtrar por tipo (RECEITA ou DESPESA)")
    public ResponseEntity<List<MovimentacaoResponse>> porTipo(
            @PathVariable Long contaId,
            @RequestParam @Parameter(description = "RECEITA ou DESPESA") TipoMovimentacao tipo) {
        return ResponseEntity.ok(movimentacaoService.listarPorTipo(contaId, tipo));
    }

    /**
     * GET /api/movimentacoes/conta/{contaId}/periodo
     *   ?inicio=2024-01-01T00:00:00&fim=2024-01-31T23:59:59
     */
    @GetMapping("/conta/{contaId}/periodo")
    @Operation(summary = "Filtrar por período")
    public ResponseEntity<List<MovimentacaoResponse>> porPeriodo(
            @PathVariable Long contaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(movimentacaoService.listarPorPeriodo(contaId, inicio, fim));
    }

    /** DELETE /api/movimentacoes/{id} — reverte o saldo e remove */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar movimentação", description = "Remove a movimentação e reverte o saldo da conta")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        movimentacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
