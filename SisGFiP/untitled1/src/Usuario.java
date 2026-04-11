public class Usuario {
    private String nomeUsuario;
    private String senha;


    public Usuario(String nomeUsuario, String senha){

        this.nomeUsuario = nomeUsuario;
        this.senha= senha;}





public boolean autenticar(String usuarioDigitado, String senhaDigitada) {
    return nomeUsuario.equals(usuarioDigitado) && senha.equals(senhaDigitada);
}}





