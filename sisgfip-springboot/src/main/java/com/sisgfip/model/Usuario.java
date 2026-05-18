package com.sisgfip.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa um usuário do sistema.
 *
 * Mapeada para a tabela `usuarios` no MySQL.
 * Relacionamento: um Usuário possui uma Conta (OneToOne).
 */
@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = @UniqueConstraint(columnNames = "nome_usuario")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Login do usuário — deve ser único no sistema */
    @NotBlank(message = "Nome de usuário é obrigatório")
    @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
    @Column(name = "nome_usuario", nullable = false, unique = true, length = 50)
    private String nomeUsuario;

    /** Senha armazenada com hash (BCrypt) — nunca em texto puro */
    @NotBlank(message = "Senha é obrigatória")
    @Column(name = "senha", nullable = false)
    private String senha;

    /** Perfil do usuário: USER ou ADMIN */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    /** Flag de conta ativa (soft delete) */
    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private boolean ativo = true;

    /** Timestamp de criação — preenchido automaticamente */
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    /** Conta financeira vinculada ao usuário (relação 1:1) */
    @OneToOne(
        mappedBy = "usuario",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private Conta conta;

    /** Preenche a data de criação antes de persistir */
    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    /** Enum de perfis de acesso */
    public enum Role {
        USER, ADMIN
    }
}
