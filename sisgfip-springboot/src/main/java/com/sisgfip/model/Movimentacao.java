package com.sisgfip.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa uma movimentação financeira (receita ou despesa).
 *
 * Mapeada para a tabela `movimentacoes` no MySQL.
 * Toda movimentação pertence a uma Conta específica.
 */
@Entity
@Table(name = "movimentacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo da movimentação.
     * RECEITA → aumenta saldo | DESPESA → diminui saldo
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    @NotNull(message = "Tipo da movimentação é obrigatório")
    private TipoMovimentacao tipo;

    /**
     * Valor da movimentação — sempre positivo.
     * O sinal (crédito/débito) é determinado pelo campo `tipo`.
     */
    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    @NotNull
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;

    /** Descrição livre: "Salário", "Conta de luz", etc. */
    @NotBlank(message = "Descrição é obrigatória")
    @Column(name = "descricao", nullable = false, length = 200)
    private String descricao;

    /** Categoria para agrupamento futuro (ex: "Alimentação", "Transporte") */
    @Column(name = "categoria", length = 50)
    private String categoria;

    /** Data e hora em que a movimentação foi registrada */
    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    /** Conta a que esta movimentação pertence */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @PrePersist
    protected void onCreate() {
        this.dataHora = LocalDateTime.now();
    }

    /** Enum dos tipos possíveis de movimentação */
    public enum TipoMovimentacao {
        RECEITA,
        DESPESA
    }
}
