import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private Usuario usuarioLogado;
    private JTextArea areaExtrato;

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;

        setTitle("Sistema Financeiro - " + usuario.getNomeUsuario());
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton botaoReceita = new JButton("Adicionar Receita");
        JButton botaoDespesa = new JButton("Adicionar Despesa");
        JButton botaoSaldo = new JButton("Mostrar Saldo");
        JButton botaoExtrato = new JButton("Mostrar Extrato");

        areaExtrato = new JTextArea();
        areaExtrato.setEditable(false);

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(botaoReceita);
        painelBotoes.add(botaoDespesa);
        painelBotoes.add(botaoSaldo);
        painelBotoes.add(botaoExtrato);

        add(painelBotoes, BorderLayout.NORTH);
        add(new JScrollPane(areaExtrato), BorderLayout.CENTER);

        botaoReceita.addActionListener(e -> adicionarReceita());
        botaoDespesa.addActionListener(e -> adicionarDespesa());
        botaoSaldo.addActionListener(e -> mostrarSaldo());
        botaoExtrato.addActionListener(e -> mostrarExtrato());

        setVisible(true);
    }

    private void adicionarReceita() {
        String valorTexto = JOptionPane.showInputDialog(this, "Digite o valor da receita:");
        if (valorTexto != null) {
            int valor = Integer.parseInt(valorTexto);
            usuarioLogado.getConta().adicionarReceita(valor);
            JOptionPane.showMessageDialog(this, "Receita adicionada.");
        }
    }

    private void adicionarDespesa() {
        String valorTexto = JOptionPane.showInputDialog(this, "Digite o valor da despesa:");
        if (valorTexto != null) {
            int valor = Integer.parseInt(valorTexto);
            usuarioLogado.getConta().adicionarDespesa(valor);
            JOptionPane.showMessageDialog(this, "Despesa adicionada.");
        }
    }

    private void mostrarSaldo() {
        int saldo = usuarioLogado.getConta().getSaldo();
        JOptionPane.showMessageDialog(this, "Saldo atual: R$ " + saldo);
    }

    private void mostrarExtrato() {
        areaExtrato.setText(usuarioLogado.getConta().gerarTextoExtrato());
    }
}