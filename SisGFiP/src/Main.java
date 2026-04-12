import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class Main {
    public static ArrayList<Usuario> usuarios = new ArrayList<>();

    public static void main(String[] args) {
        usuarios.add(new Usuario("admin", "1234"));
        usuarios.add(new Usuario("joao", "abcd"));

        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = new TelaLogin(usuarios);
            telaLogin.setVisible(true);
        });
    }
}