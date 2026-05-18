package com.sisgfip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação SisGFiP.
 *
 * @SpringBootApplication habilita:
 *   - @Configuration     → beans de configuração
 *   - @EnableAutoConfiguration → auto-configuração do Spring Boot
 *   - @ComponentScan     → varredura de componentes no pacote raiz
 */
@SpringBootApplication
public class SisGFiPApplication {

    public static void main(String[] args) {
        SpringApplication.run(SisGFiPApplication.class, args);
    }
}
