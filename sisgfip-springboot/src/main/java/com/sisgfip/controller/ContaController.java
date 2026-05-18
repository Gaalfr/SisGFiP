package com.sisgfip.controller;

import com.sisgfip.dto.DTOs.*;
import com.sisgfip.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de Conta.
 * Base URL: /api/contas
 */
@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Gerenciamento de contas financeiras")
public class ContaController {

    private final ContaService contaService;

    /** POST /api/contas */
    @PostMapping
    @Operation(summary = "Criar conta")
    public ResponseEntity<ContaResponse> criar(@RequestBody @Valid ContaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(contaService.criar(request));
    }

    /** GET /api/contas/{id} */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID")
    public ResponseEntity<ContaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    /** GET /api/contas/usuario/{usuarioId} — todas as contas de um usuário */
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar contas por usuário")
    public ResponseEntity<List<ContaResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(contaService.listarPorUsuario(usuarioId));
    }

    /** GET /api/contas/{id}/resumo — saldo + totais de receita/despesa */
    @GetMapping("/{id}/resumo")
    @Operation(summary = "Resumo financeiro da conta")
    public ResponseEntity<ContaResumo> resumo(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarResumo(id));
    }

    /** PUT /api/contas/{id} */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar nome da conta")
    public ResponseEntity<ContaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ContaRequest request) {
        return ResponseEntity.ok(contaService.atualizar(id, request));
    }

    /** DELETE /api/contas/{id} */
    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar conta")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        contaService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
