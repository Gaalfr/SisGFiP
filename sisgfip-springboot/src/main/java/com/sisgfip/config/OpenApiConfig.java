package com.sisgfip.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger / OpenAPI 3.
 *
 * Acesso: http://localhost:8080/api/swagger-ui.html
 * JSON:   http://localhost:8080/api/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SisGFiP API")
                .description("""
                    **Sistema de Gestão Financeira Pessoal**
                    
                    API REST para gerenciamento de usuários, contas e movimentações financeiras.
                    
                    **Fluxo básico:**
                    1. `POST /usuarios` — cadastrar usuário (já cria conta padrão)
                    2. `POST /auth/login` — fazer login
                    3. `POST /movimentacoes` — registrar receita ou despesa
                    4. `GET /contas/{id}/resumo` — ver saldo e totais
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("SisGFiP")
                    .email("contato@sisgfip.com"))
                .license(new License()
                    .name("MIT")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server().url("http://localhost:8080/api").description("Desenvolvimento")
            ));
    }
}
