package com.sisgfip.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa a conta financeira de um usuário.
 *
 * Mapeada para a tabela `contas` no MySQL.
 * Relacionamentos:
 *   - ManyToOne → Usuario (dono da conta)
 *   - OneToMany → Movimentacao (extrato completo)
 *
 * Nota: saldo usa BigDecimal para precisão monetária correta.
 * Usar int/double para dinheiro é um bug clássico.
 */
@Entity
@Table(name = "contas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome ou apelido da conta (ex: "Conta Corrente", "Poupança") */
    @NotBlank(message = "Nome da conta é obrigatório")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    /**
     * Saldo atual da conta.
     * precision=15, scale=2 → até 9.999.999.999.999,99
     */
    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal saldo = BigDecimal.ZERO;

    /** Dono da conta */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Extrato: lista de todas as movimentações desta conta */
    @OneToMany(
        mappedBy = "conta",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    @Builder.Default
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    /** Flag de conta ativa */
    @Column(name = "ativa", nullable = false)
    @Builder.Default
    private boolean ativa = true;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @PrePersist
    protected void onCreate() {
        this.criadaEm = LocalDateTime.now();
    }

    // ── Métodos de negócio ───────────────────────────────────────

    /**
     * Adiciona receita ao saldo e retorna a movimentação gerada.
     * A movimentação ainda precisa ser persistida via repository.
     */
    public void creditar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de crédito deve ser positivo");
        }
        this.saldo = this.saldo.add(valor);
    }

    /**
     * Debita despesa do saldo.
     * Permite saldo negativo (cheque especial) — adicione validação se necessário.
     */
    public void debitar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor de débito deve ser positivo");
        }
        this.saldo = this.saldo.subtract(valor);
    }
}
