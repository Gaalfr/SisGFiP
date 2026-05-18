package com.sisgfip.swing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * ╔════════════════════════════════════════════════════════════╗
 * ║   Cliente Swing adaptado para consumir a API REST          ║
 * ║                                                            ║
 * ║   ANTES: lógica direta em ArrayList em memória             ║
 * ║   DEPOIS: chamadas HTTP para a API Spring Boot             ║
 * ║                                                            ║
 * ║   Inicie a API antes de usar: mvn spring-boot:run          ║
 * ╚════════════════════════════════════════════════════════════╝
 */
public class TelaLogin extends JFrame {

    private static final String BASE_URL = "http://localhost:8080/api";

    // Cliente HTTP nativo do Java 11+
    private static final HttpClient HTTP = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    // ── Componentes da tela ──────────────────────────────────────
    private final JTextField campoUsuario = new JTextField(15);
    private final JPasswordField campoSenha = new JPasswordField(15);
    private final JButton botaoLogin = new JButton("Entrar");
    private final JButton botaoCadastrar = new JButton("Cadastrar");

    public TelaLogin() {
        setTitle("SisGFiP — Login");
        setSize(380, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        montarTela();
    }

    private void montarTela() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titulo = new JLabel("Sistema de Gestão Financeira", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        painel.add(titulo, gbc);
        gbc.gridwidth = 1;

        // Usuário
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        painel.add(campoUsuario, gbc);

        // Senha
        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        painel.add(campoSenha, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(botaoLogin);
        painelBotoes.add(botaoCadastrar);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        painel.add(painelBotoes, gbc);

        add(painel);

        botaoLogin.addActionListener(e -> fazerLogin());
        botaoCadastrar.addActionListener(e -> fazerCadastro());
    }

    /**
     * Faz login consumindo POST /api/auth/login
     *
     * ANTES (sem API):
     *   for (Usuario u : usuarios) { if (u.autenticar(...)) ... }
     *
     * DEPOIS (com API REST):
     *   HTTP POST → JSON → verificar resposta
     */
    private void fazerLogin() {
        String usuario = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha.");
            return;
        }

        botaoLogin.setEnabled(false);
        botaoLogin.setText("Aguarde...");

        // Executa em thread separada para não travar a UI (EDT)
        new Thread(() -> {
            try {
                String body = """
                    {"nomeUsuario": "%s", "senha": "%s"}
                    """.formatted(usuario, senha);

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(10))
                    .build();

                HttpResponse<String> response = HTTP.send(request,
                    HttpResponse.BodyHandlers.ofString());

                SwingUtilities.invokeLater(() -> {
                    botaoLogin.setEnabled(true);
                    botaoLogin.setText("Entrar");

                    if (response.statusCode() == 200) {
                        JsonNode json = parseJson(response.body());
                        long usuarioId = json.path("usuarioId").asLong();
                        String nomeUsuario = json.path("nomeUsuario").asText();

                        JOptionPane.showMessageDialog(this,
                            "Bem-vindo, " + nomeUsuario + "!");
                        dispose();
                        new TelaPrincipal(usuarioId, nomeUsuario).setVisible(true);

                    } else {
                        JsonNode json = parseJson(response.body());
                        String msg = json.path("mensagem").asText("Usuário ou senha incorretos");
                        JOptionPane.showMessageDialog(this, msg,
                            "Erro de Login", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    botaoLogin.setEnabled(true);
                    botaoLogin.setText("Entrar");
                    JOptionPane.showMessageDialog(this,
                        "Não foi possível conectar à API.\nVerifique se o servidor está rodando em: " + BASE_URL,
                        "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    /** Cadastra novo usuário via POST /api/usuarios */
    private void fazerCadastro() {
        String usuario = JOptionPane.showInputDialog(this, "Nome de usuário:");
        if (usuario == null || usuario.isBlank()) return;

        JPasswordField senhaField = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this, senhaField,
            "Senha (mín. 6 caracteres):", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        String senha = new String(senhaField.getPassword());

        new Thread(() -> {
            try {
                String body = """
                    {"nomeUsuario": "%s", "senha": "%s"}
                    """.formatted(usuario, senha);

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/usuarios"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

                HttpResponse<String> response = HTTP.send(request,
                    HttpResponse.BodyHandlers.ofString());

                SwingUtilities.invokeLater(() -> {
                    if (response.statusCode() == 201) {
                        JOptionPane.showMessageDialog(this,
                            "Usuário criado com sucesso!\nFaça login para continuar.");
                        campoUsuario.setText(usuario);
                        campoSenha.setText("");
                        campoSenha.requestFocus();
                    } else {
                        JsonNode json = parseJson(response.body());
                        JOptionPane.showMessageDialog(this,
                            json.path("mensagem").asText("Erro ao criar usuário"),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this,
                        "Erro de conexão: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private JsonNode parseJson(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (Exception e) {
            return MAPPER.createObjectNode();
        }
    }

    // ═══════════════════════════════════════════════════════════
    //   TELA PRINCIPAL
    // ═══════════════════════════════════════════════════════════

    public static class TelaPrincipal extends JFrame {

        private final long usuarioId;
        private final String nomeUsuario;
        private Long contaId; // será buscado na API

        private final JTextArea areaExtrato = new JTextArea();
        private final JLabel labelSaldo = new JLabel("Saldo: carregando...");

        public TelaPrincipal(long usuarioId, String nomeUsuario) {
            this.usuarioId = usuarioId;
            this.nomeUsuario = nomeUsuario;

            setTitle("SisGFiP — " + nomeUsuario);
            setSize(600, 480);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            montarTela();
            carregarConta(); // busca a conta do usuário na API
        }

        private void montarTela() {
            JButton btnReceita = new JButton("+ Receita");
            JButton btnDespesa = new JButton("- Despesa");
            JButton btnExtrato = new JButton("Extrato");
            JButton btnAtualizar = new JButton("Atualizar");

            btnReceita.setBackground(new Color(76, 175, 80));
            btnReceita.setForeground(Color.WHITE);
            btnDespesa.setBackground(new Color(244, 67, 54));
            btnDespesa.setForeground(Color.WHITE);

            labelSaldo.setFont(new Font("SansSerif", Font.BOLD, 16));
            labelSaldo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel topo = new JPanel(new BorderLayout());
            topo.add(labelSaldo, BorderLayout.WEST);
            JPanel botoes = new JPanel();
            botoes.add(btnReceita);
            botoes.add(btnDespesa);
            botoes.add(btnExtrato);
            botoes.add(btnAtualizar);
            topo.add(botoes, BorderLayout.EAST);

            areaExtrato.setEditable(false);
            areaExtrato.setFont(new Font("Monospaced", Font.PLAIN, 12));

            add(topo, BorderLayout.NORTH);
            add(new JScrollPane(areaExtrato), BorderLayout.CENTER);

            btnReceita.addActionListener(e -> registrarMovimentacao("RECEITA"));
            btnDespesa.addActionListener(e -> registrarMovimentacao("DESPESA"));
            btnExtrato.addActionListener(e -> carregarExtrato());
            btnAtualizar.addActionListener(e -> { carregarConta(); carregarExtrato(); });
        }

        /** Busca a primeira conta do usuário via GET /api/contas/usuario/{id} */
        private void carregarConta() {
            new Thread(() -> {
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/contas/usuario/" + usuarioId))
                        .GET().build();

                    HttpResponse<String> resp = HTTP.send(req,
                        HttpResponse.BodyHandlers.ofString());

                    SwingUtilities.invokeLater(() -> {
                        if (resp.statusCode() == 200) {
                            try {
                                JsonNode json = MAPPER.readTree(resp.body());
                                JsonNode primeira = json.get(0);
                                if (primeira != null) {
                                    contaId = primeira.path("id").asLong();
                                    String saldo = primeira.path("saldo").asText("0");
                                    labelSaldo.setText("Saldo: R$ " + saldo);
                                    carregarExtrato();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                        labelSaldo.setText("Erro ao carregar conta"));
                }
            }).start();
        }

        /**
         * Registra movimentação via POST /api/movimentacoes
         *
         * ANTES: usuarioLogado.getConta().adicionarReceita(valor)
         * DEPOIS: POST JSON para a API
         */
        private void registrarMovimentacao(String tipo) {
            if (contaId == null) {
                JOptionPane.showMessageDialog(this, "Conta não carregada ainda.");
                return;
            }

            String valorStr = JOptionPane.showInputDialog(this,
                "Digite o valor da " + tipo.toLowerCase() + ":");
            if (valorStr == null || valorStr.isBlank()) return;

            String descricao = JOptionPane.showInputDialog(this, "Descrição:");
            if (descricao == null || descricao.isBlank()) descricao = tipo;

            final String descFinal = descricao;

            try {
                BigDecimal valor = new BigDecimal(valorStr.replace(",", "."));

                new Thread(() -> {
                    try {
                        String body = """
                            {
                              "tipo": "%s",
                              "valor": %s,
                              "descricao": "%s",
                              "contaId": %d
                            }
                            """.formatted(tipo, valor.toPlainString(), descFinal, contaId);

                        HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create(BASE_URL + "/movimentacoes"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(body))
                            .build();

                        HttpResponse<String> resp = HTTP.send(req,
                            HttpResponse.BodyHandlers.ofString());

                        SwingUtilities.invokeLater(() -> {
                            if (resp.statusCode() == 201) {
                                JOptionPane.showMessageDialog(this,
                                    tipo + " registrada com sucesso!");
                                carregarConta();
                            } else {
                                try {
                                    JsonNode json = MAPPER.readTree(resp.body());
                                    JOptionPane.showMessageDialog(this,
                                        json.path("mensagem").asText("Erro"),
                                        "Erro", JOptionPane.ERROR_MESSAGE);
                                } catch (Exception ignored) {}
                            }
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this,
                                "Erro: " + ex.getMessage()));
                    }
                }).start();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor inválido: " + valorStr);
            }
        }

        /** Carrega extrato via GET /api/movimentacoes/conta/{id} */
        private void carregarExtrato() {
            if (contaId == null) return;

            new Thread(() -> {
                try {
                    HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/movimentacoes/conta/" + contaId + "?size=50"))
                        .GET().build();

                    HttpResponse<String> resp = HTTP.send(req,
                        HttpResponse.BodyHandlers.ofString());

                    SwingUtilities.invokeLater(() -> {
                        if (resp.statusCode() == 200) {
                            try {
                                JsonNode page = MAPPER.readTree(resp.body());
                                JsonNode conteudo = page.path("content");
                                StringBuilder sb = new StringBuilder("=== EXTRATO ===\n\n");

                                for (JsonNode mov : conteudo) {
                                    String tipo = mov.path("tipo").asText();
                                    String valor = mov.path("valor").asText();
                                    String desc = mov.path("descricao").asText();
                                    String data = mov.path("dataHora").asText().replace("T", " ");
                                    String sinal = "RECEITA".equals(tipo) ? "+" : "-";
                                    sb.append(String.format("%-8s %sR$ %-12s  %-30s  %s%n",
                                        tipo, sinal, valor, desc, data.substring(0, 16)));
                                }

                                areaExtrato.setText(sb.toString());
                                areaExtrato.setCaretPosition(0);
                            } catch (Exception ex) {
                                areaExtrato.setText("Erro ao parsear extrato: " + ex.getMessage());
                            }
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                        areaExtrato.setText("Erro ao carregar extrato: " + ex.getMessage()));
                }
            }).start();
        }
    }

    // ── Ponto de entrada do Swing ────────────────────────────────
    public static void main(String[] args) {
        // Usa o Look and Feel do sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
