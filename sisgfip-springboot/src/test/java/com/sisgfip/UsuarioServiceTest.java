package com.sisgfip;

import com.sisgfip.dto.DTOs.*;
import com.sisgfip.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes de integração — usa H2 em memória (profile "test").
 * Cria contexto Spring completo para testar a pilha inteira.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuario() {
        var request = new UsuarioRequest("teste_user", "senha123");
        var response = usuarioService.criar(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.nomeUsuario()).isEqualTo("teste_user");
        assertThat(response.ativo()).isTrue();
    }

    @Test
    @DisplayName("Deve autenticar usuário válido")
    void deveAutenticarUsuarioValido() {
        usuarioService.criar(new UsuarioRequest("joao", "senha456"));

        var loginResp = usuarioService.login(new LoginRequest("joao", "senha456"));

        assertThat(loginResp.usuarioId()).isNotNull();
        assertThat(loginResp.nomeUsuario()).isEqualTo("joao");
    }

    @Test
    @DisplayName("Não deve criar usuário duplicado")
    void naoDeveCriarUsuarioDuplicado() {
        usuarioService.criar(new UsuarioRequest("duplicado", "senha"));

        assertThatThrownBy(() ->
            usuarioService.criar(new UsuarioRequest("duplicado", "outra"))
        ).hasMessageContaining("duplicado");
    }
}
