package com.sisgfip.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de Segurança do Spring Security.
 *
 * Estado atual: API aberta para desenvolvimento.
 * Para produção: descomente os blocos marcados com "PRODUÇÃO".
 *
 * Quando JWT for implementado, adicionar o filtro JwtAuthenticationFilter
 * antes do UsernamePasswordAuthenticationFilter.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define quais endpoints são públicos e quais exigem autenticação.
     *
     * Configuração atual (DEV): tudo liberado para facilitar testes.
     * Troque por authorizeHttpRequests configurado para produção.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF — desnecessário para API REST stateless
            .csrf(AbstractHttpConfigurer::disable)

            // API REST não usa sessão HTTP
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ── DEV: libera todos os endpoints ───────────────────────
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

            // ── PRODUÇÃO: remova o bloco acima e use este: ───────────
            // .authorizeHttpRequests(auth -> auth
            //     .requestMatchers("/auth/**").permitAll()
            //     .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            //     .anyRequest().authenticated()
            // )
            // .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt com fator 12 — bom equilíbrio entre segurança e performance.
     * Fator 10 é o padrão; 12 é mais seguro e ainda aceitável.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
