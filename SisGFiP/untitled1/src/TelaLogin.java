import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TelaLogin extends JFrame {

    private JTextField campoUsuario;
    private JPasswordField campoSenha;
    private JButton botaoLogin;
    private ArrayList<Usuario> usuarios;

    public TelaLogin(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;

        setTitle("Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(3, 2, 10, 10));

        painel.add(new JLabel("Usuário:"));
        campoUsuario = new JTextField();
        painel.add(campoUsuario);

        painel.add(new JLabel("Senha:"));
        campoSenha = new JPasswordField();
        painel.add(campoSenha);

        botaoLogin = new JButton("Entrar");
        painel.add(new JLabel());
        painel.add(botaoLogin);

        add(painel);

        botaoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fazerLogin();
            }
        });
    }

    private void fazerLogin() {
        String usuarioDigitado = campoUsuario.getText();
        String senhaDigitada = new String(campoSenha.getPassword());

        for (Usuario usuario : usuarios) {
            if (usuario.autenticar(usuarioDigitado, senhaDigitada)) {
                JOptionPane.showMessageDialog(this, "Login realizado com sucesso!");
                new TelaPrincipal(usuario);
                dispose();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.");
    }
}