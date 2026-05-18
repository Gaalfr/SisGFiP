package com.sisgfip.repository;

import com.sisgfip.model.Movimentacao;
import com.sisgfip.model.Movimentacao.TipoMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório JPA para Movimentacao.
 *
 * Usa Page<T> para suportar paginação — importante ao listar extratos
 * que podem ter centenas de registros.
 */
@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    /** Extrato completo de uma conta, ordenado por data decrescente — paginado */
    Page<Movimentacao> findByContaIdOrderByDataHoraDesc(Long contaId, Pageable pageable);

    /** Extrato filtrado por tipo (RECEITA ou DESPESA) */
    List<Movimentacao> findByContaIdAndTipoOrderByDataHoraDesc(Long contaId, TipoMovimentacao tipo);

    /** Movimentações em um período */
    @Query("""
        SELECT m FROM Movimentacao m
        WHERE m.conta.id = :contaId
          AND m.dataHora BETWEEN :inicio AND :fim
        ORDER BY m.dataHora DESC
        """)
    List<Movimentacao> findByContaIdAndPeriodo(
        @Param("contaId") Long contaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim")    LocalDateTime fim
    );

    /** Soma das receitas de uma conta */
    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Movimentacao m WHERE m.conta.id = :contaId AND m.tipo = 'RECEITA'")
    BigDecimal sumReceitasByContaId(@Param("contaId") Long contaId);

    /** Soma das despesas de uma conta */
    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM Movimentacao m WHERE m.conta.id = :contaId AND m.tipo = 'DESPESA'")
    BigDecimal sumDespesasByContaId(@Param("contaId") Long contaId);
}
