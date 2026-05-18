package com.sisgfip.repository;

import com.sisgfip.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Conta.
 */
@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    /** Todas as contas de um usuário */
    List<Conta> findByUsuarioId(Long usuarioId);

    /** Conta ativa de um usuário pelo ID */
    Optional<Conta> findByIdAndUsuarioIdAndAtivaTrue(Long contaId, Long usuarioId);

    /** Verifica se o usuário possui alguma conta */
    boolean existsByUsuarioId(Long usuarioId);

    /** Soma total do saldo de todas as contas de um usuário */
    @Query("SELECT COALESCE(SUM(c.saldo), 0) FROM Conta c WHERE c.usuario.id = :usuarioId AND c.ativa = true")
    java.math.BigDecimal sumSaldoByUsuarioId(@Param("usuarioId") Long usuarioId);
}
