package com.sisgfip.controller;

import com.sisgfip.dto.DTOs.*;
import com.sisgfip.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de Usuário.
 *
 * Base URL: /api/usuarios
 *
 * Responsabilidade: receber request HTTP, delegar ao Service,
 * retornar resposta HTTP. Nenhuma lógica de negócio aqui.
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /** POST /api/usuarios — Cadastra novo usuário */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar usuário", description = "Cria um novo usuário com conta padrão")
    public ResponseEntity<UsuarioResponse> criar(@RequestBody @Valid UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(usuarioService.criar(request));
    }

    /** GET /api/usuarios — Lista todos os usuários ativos */
    @GetMapping
    @Operation(summary = "Listar usuários")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    /** GET /api/usuarios/{id} — Busca usuário por ID */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    /** DELETE /api/usuarios/{id} — Desativa usuário (soft delete) */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativar usuário")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
