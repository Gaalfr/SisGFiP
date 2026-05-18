package com.sisgfip.config;

import com.sisgfip.dto.DTOs.UsuarioRequest;
import com.sisgfip.repository.UsuarioRepository;
import com.sisgfip.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Popula o banco com dados iniciais ao subir a aplicação.
 * Só executa nos profiles "dev" e "default".
 *
 * Para usar: inicie com --spring.profiles.active=dev
 * Para desabilitar: não use este profile em produção.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Bean
    @Profile({"dev", "default"})
    public CommandLineRunner seed() {
        return args -> {
            if (usuarioRepository.count() > 0) {
                log.info("Banco já populado — seed ignorado.");
                return;
            }

            log.info("Populando banco com dados iniciais...");

            // Replica os usuários do Main.java original
            usuarioService.criar(new UsuarioRequest("admin", "1234"));
            usuarioService.criar(new UsuarioRequest("joao", "abcd"));

            log.info("Seed concluído. Usuários criados: admin / joao");
            log.info("Swagger disponível em: http://localhost:8080/api/swagger-ui.html");
        };
    }
}
