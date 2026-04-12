import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    @Test
    void deveIniciarComSaldoZero() {
        Conta conta = new Conta();

        assertEquals(0, conta.getSaldo());
    }

    @Test
    void deveAdicionarReceitaAoSaldo() {
        Conta conta = new Conta();

        conta.adicionarReceita(100);

        assertEquals(100, conta.getSaldo());
    }

    @Test
    void deveSubtrairDespesaDoSaldo() {
        Conta conta = new Conta();

        conta.adicionarReceita(200);
        conta.adicionarDespesa(50);

        assertEquals(150, conta.getSaldo());
    }

    @Test
    void devePermitirSaldoNegativoQuandoDespesaForMaiorQueSaldo() {
        Conta conta = new Conta();

        conta.adicionarDespesa(80);

        assertEquals(-80, conta.getSaldo());
    }

    @Test
    void deveGerarMensagemQuandoExtratoEstiverVazio() {
        Conta conta = new Conta();

        String extrato = conta.gerarTextoExtrato();

        assertEquals("Nenhuma movimentação registrada.", extrato);
    }

    @Test
    void deveGerarExtratoComReceitaEDespesa() {
        Conta conta = new Conta();

        conta.adicionarReceita(300);
        conta.adicionarDespesa(120);

        String extrato = conta.gerarTextoExtrato();

        assertAll(
                () -> assertTrue(extrato.contains("EXTRATO")),
                () -> assertTrue(extrato.contains("Receita")),
                () -> assertTrue(extrato.contains("Despesa")),
                () -> assertTrue(extrato.contains("Saldo atual: R$ 180"))
        );
    }
}