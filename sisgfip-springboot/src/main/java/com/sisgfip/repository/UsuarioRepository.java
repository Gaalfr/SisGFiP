package com.sisgfip.repository;

import com.sisgfip.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Usuario.
 *
 * JpaRepository já fornece:
 *   - save(), findById(), findAll(), deleteById(), count(), existsById()...
 *
 * Aqui apenas adicionamos queries específicas do negócio.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Busca por nome de usuário (para login) */
    Optional<Usuario> findByNomeUsuario(String nomeUsuario);

    /** Verifica se já existe um usuário com esse nome (para cadastro) */
    boolean existsByNomeUsuario(String nomeUsuario);

    /** Busca usuários ativos apenas */
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true")
    java.util.List<Usuario> findAllAtivos();
}
