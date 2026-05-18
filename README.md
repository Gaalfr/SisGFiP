#  Sistema Financeiro em Java

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Status](https://img.shields.io/badge/status-in%20progress-yellow?style=for-the-badge)
![OOP](https://img.shields.io/badge/OOP-Object%20Oriented-blue?style=for-the-badge)

Projeto desenvolvido em Java com foco em **Programação Orientada a Objetos (POO)** e construção de **interface gráfica (GUI)** utilizando Swing.

O sistema simula um ambiente de controle financeiro com múltiplos usuários, autenticação e registro de movimentações.

---


##  Demonstração

![Demonstração do sistema](Demo.gif)

>  Caso o GIF não apareça, verifique se o arquivo `demo.gif` está na raiz do projeto.

---
##  Instruções de uso

Ao iniciar o programa, a tela de login será exibida.

### . Fazer login
Use um dos usuários padrão cadastrados no sistema:


Usuário: admin
Senha: 1234
## Funcionalidades

- Login de usuários
- Cadastro de novos usuários
- Registro de receitas
- Registro de despesas
- Visualização de saldo
- Exibição de extrato
- Logout

---

##  Interface

O sistema utiliza **Java Swing** para interface gráfica, com:

- Tela de login
- Tela principal com botões de ação
- Área de exibição de extrato
- Caixas de diálogo para entrada de dados

---

##  Arquitetura do Projeto

O projeto segue uma separação clara de responsabilidades:

- **Usuario** → autenticação e dados do usuário  
- **Conta** → lógica financeira (saldo e operações)  
- **Movimentacao** → representação das transações  
- **TelaLogin / TelaPrincipal** → interface gráfica  
- **Main** → inicialização do sistema  

---

##  Estrutura do Projeto

```text
SistemaFinanceiro/
├── Main.java
├── Usuario.java
├── Conta.java
├── Movimentacao.java
├── TelaLogin.java
├── TelaPrincipal.java
└── demo.gif
# SisGFiP — Sistema de Gestão Financeira Pessoal
### Spring Boot 3 + API REST + MySQL

---

## 📐 Arquitetura

```
┌─────────────────────────────────────────────────┐
│            Frontend (Swing)                      │
│   TelaLogin.java  →  HTTP Client (Java 11+)      │
└──────────────────────────┬──────────────────────┘
                           │ JSON / HTTP
┌──────────────────────────▼──────────────────────┐
│         API REST (Spring Boot 3)                 │
│                                                  │
│  AuthController      /api/auth/login             │
│  UsuarioController   /api/usuarios               │
│  ContaController     /api/contas                 │
│  MovimentacaoController /api/movimentacoes       │
└──────────────────────────┬──────────────────────┘
                           │
┌──────────────────────────▼──────────────────────┐
│         Service Layer                            │
│  UsuarioService  ContaService  MovimentacaoService│
└──────────────────────────┬──────────────────────┘
                           │
┌──────────────────────────▼──────────────────────┐
│         Repository Layer (Spring Data JPA)       │
│  UsuarioRepository  ContaRepository  MovimentacaoRepository │
└──────────────────────────┬──────────────────────┘
                           │
┌──────────────────────────▼──────────────────────┐
│              MySQL                               │
│  tabelas: usuarios, contas, movimentacoes        │
└─────────────────────────────────────────────────┘
```

---

## 🚀 Como rodar

### 1. Pré-requisitos
- Java 17+
- Maven 3.8+
- MySQL 8+

### 2. Criar o banco de dados MySQL
```sql
CREATE DATABASE sisgfip CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar credenciais
Edite `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
```

### 4. Subir a API
```bash
mvn spring-boot:run
```

### 5. Acessar o Swagger
```
http://localhost:8080/api/swagger-ui.html
```

---

## 📂 Estrutura do projeto

```
src/main/java/com/sisgfip/
├── SisGFiPApplication.java      ← ponto de entrada Spring Boot
├── config/
│   ├── DataSeeder.java          ← dados iniciais (admin/1234, joao/abcd)
│   ├── OpenApiConfig.java       ← configuração Swagger
│   └── SecurityConfig.java      ← Spring Security + BCrypt
├── controller/
│   ├── AuthController.java      ← POST /auth/login
│   ├── ContaController.java     ← CRUD /contas
│   ├── MovimentacaoController.java ← CRUD /movimentacoes
│   └── UsuarioController.java   ← CRUD /usuarios
├── dto/
│   └── DTOs.java                ← todos os Records de request/response
├── exception/
│   ├── Exceptions.java          ← exceções de domínio
│   └── GlobalExceptionHandler.java ← tratamento global de erros
├── model/
│   ├── Conta.java               ← entidade JPA
│   ├── Movimentacao.java        ← entidade JPA
│   └── Usuario.java             ← entidade JPA
├── repository/
│   ├── ContaRepository.java
│   ├── MovimentacaoRepository.java
│   └── UsuarioRepository.java
├── service/
│   ├── ContaService.java
│   ├── MovimentacaoService.java
│   └── UsuarioService.java
└── swing/
    └── TelaLogin.java           ← Swing adaptado para consumir a API
```

---

## 🔌 Endpoints REST

### Autenticação
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/auth/login` | Login |

**Body login:**
```json
{
  "nomeUsuario": "admin",
  "senha": "1234"
}
```

### Usuários
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/usuarios` | Criar usuário |
| GET | `/api/usuarios` | Listar todos |
| GET | `/api/usuarios/{id}` | Buscar por ID |
| DELETE | `/api/usuarios/{id}` | Desativar |

**Body criar usuário:**
```json
{
  "nomeUsuario": "maria",
  "senha": "senha123"
}
```

### Contas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/contas` | Criar conta |
| GET | `/api/contas/{id}` | Buscar por ID |
| GET | `/api/contas/usuario/{usuarioId}` | Contas de um usuário |
| GET | `/api/contas/{id}/resumo` | Saldo + totais |
| PUT | `/api/contas/{id}` | Atualizar nome |
| DELETE | `/api/contas/{id}` | Desativar |

### Movimentações
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/movimentacoes` | Registrar receita/despesa |
| GET | `/api/movimentacoes/{id}` | Buscar por ID |
| GET | `/api/movimentacoes/conta/{id}` | Extrato paginado |
| GET | `/api/movimentacoes/conta/{id}/tipo?tipo=RECEITA` | Filtrar por tipo |
| GET | `/api/movimentacoes/conta/{id}/periodo?inicio=...&fim=...` | Filtrar por período |
| DELETE | `/api/movimentacoes/{id}` | Deletar (reverte saldo) |

**Body registrar movimentação:**
```json
{
  "tipo": "RECEITA",
  "valor": 3500.00,
  "descricao": "Salário de janeiro",
  "categoria": "Trabalho",
  "contaId": 1
}
```

---

## 🧪 Exemplos com curl

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nomeUsuario":"admin","senha":"1234"}'

# Criar usuário
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nomeUsuario":"maria","senha":"abc123"}'

# Listar contas do usuário 1
curl http://localhost:8080/api/contas/usuario/1

# Registrar receita na conta 1
curl -X POST http://localhost:8080/api/movimentacoes \
  -H "Content-Type: application/json" \
  -d '{"tipo":"RECEITA","valor":5000,"descricao":"Salário","contaId":1}'

# Registrar despesa
curl -X POST http://localhost:8080/api/movimentacoes \
  -H "Content-Type: application/json" \
  -d '{"tipo":"DESPESA","valor":150,"descricao":"Conta de luz","categoria":"Moradia","contaId":1}'

# Ver resumo da conta 1
curl http://localhost:8080/api/contas/1/resumo

# Extrato paginado (página 0, 10 por página)
curl "http://localhost:8080/api/movimentacoes/conta/1?page=0&size=10"
```

---

## 🔐 Roadmap JWT (próximos passos)

1. Criar `JwtService` com `io.jsonwebtoken` (já no pom.xml)
2. Criar `JwtAuthenticationFilter extends OncePerRequestFilter`
3. Em `SecurityConfig`, adicionar o filtro antes de `UsernamePasswordAuthenticationFilter`
4. No `AuthController`, retornar o token no campo `token` da `LoginResponse`
5. No Swing, guardar o token e enviar no header: `Authorization: Bearer <token>`

---

## 💡 Diferenças em relação ao projeto original

| Antes (Swing puro) | Depois (Spring Boot + REST) |
|--------------------|-----------------------------|
| `ArrayList<Usuario> usuarios` | Tabela `usuarios` no MySQL |
| `usuarios.add(new Usuario(...))` | `POST /api/usuarios` |
| `usuario.autenticar(nome, senha)` | `POST /api/auth/login` (BCrypt) |
| `conta.adicionarReceita(valor)` | `POST /api/movimentacoes` |
| `conta.getSaldo()` | `GET /api/contas/{id}/resumo` |
| `conta.gerarTextoExtrato()` | `GET /api/movimentacoes/conta/{id}` |
| Dados perdidos ao fechar | Persistência permanente no MySQL |
| Usuário único por sessão | Multi-usuário simultâneo |
