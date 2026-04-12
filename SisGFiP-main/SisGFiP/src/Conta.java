import java.util.ArrayList;

public class Conta {
    private int saldo;
    private ArrayList<Movimentacao> extrato;

    public Conta() {
        this.saldo = 0;
        this.extrato = new ArrayList<>();
    }

    public void adicionarReceita(int valor) {
        saldo += valor;
        extrato.add(new Movimentacao("Receita", valor));
    }

    public void adicionarDespesa(int valor) {
        saldo -= valor;
        extrato.add(new Movimentacao("Despesa", valor));
    }

    public int getSaldo() {
        return saldo;
    }

    public String gerarTextoExtrato() {
        if (extrato.isEmpty()) {
            return "Nenhuma movimentação registrada.";
        }

        StringBuilder texto = new StringBuilder();
        texto.append("=== EXTRATO ===\n");

        for (Movimentacao m : extrato) {
            texto.append(m.toString()).append("\n");
        }

        texto.append("\nSaldo atual: R$ ").append(saldo);

        return texto.toString();
    }
}