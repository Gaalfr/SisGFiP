import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void deveAutenticarQuandoUsuarioESenhaForemCorretos() {
        Usuario usuario = new Usuario("admin", "1234");

        boolean autenticado = usuario.autenticar("admin", "1234");

        assertTrue(autenticado);
    }

    @Test
    void naoDeveAutenticarQuandoSenhaEstiverErrada() {
        Usuario usuario = new Usuario("admin", "1234");

        boolean autenticado = usuario.autenticar("admin", "9999");

        assertFalse(autenticado);
    }

    @Test
    void naoDeveAutenticarQuandoUsuarioEstiverErrado() {
        Usuario usuario = new Usuario("admin", "1234");

        boolean autenticado = usuario.autenticar("joao", "1234");

        assertFalse(autenticado);
    }

    @Test
    void deveRetornarNomeDoUsuario() {
        Usuario usuario = new Usuario("joao", "abcd");

        assertEquals("joao", usuario.getNomeUsuario());
    }

    @Test
    void deveCriarContaAutomaticamenteParaONovoUsuario() {
        Usuario usuario = new Usuario("joao", "abcd");

        assertNotNull(usuario.getConta());
        assertEquals(0, usuario.getConta().getSaldo());
    }
}