import java.time.LocalDateTime;

public class Movimentacao {
    private String tipo;
    private int valor;
    private LocalDateTime data;

    public Movimentacao(String tipo, int valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.data = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return tipo + ": R$ " + valor + " | " + data;
    }
}
