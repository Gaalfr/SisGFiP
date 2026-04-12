public class Usuario {
    private String nomeUsuario;
    private String senha;
    private Conta conta;

    public Usuario(String nomeUsuario, String senha) {
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
        this.conta = new Conta();
    }

    public boolean autenticar(String usuarioDigitado, String senhaDigitada) {
        return nomeUsuario.equals(usuarioDigitado) && senha.equals(senhaDigitada);
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public Conta getConta() {
        return conta;
    }
}




