package com.sisgfip.controller;

import com.sisgfip.dto.DTOs.*;
import com.sisgfip.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de Autenticação.
 *
 * POST /api/auth/login — valida credenciais e retorna dados do usuário.
 * No futuro: retornará JWT no campo `token`.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e autenticação")
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(
        summary = "Fazer login",
        description = "Autentica o usuário e retorna seus dados. " +
                      "O campo 'token' será preenchido quando JWT for ativado."
    )
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }
}
