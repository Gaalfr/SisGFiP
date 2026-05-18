package com.sisgfip.service;

import com.sisgfip.dto.DTOs.*;
import com.sisgfip.exception.Exceptions.*;
import com.sisgfip.model.Conta;
import com.sisgfip.model.Movimentacao;
import com.sisgfip.model.Movimentacao.TipoMovimentacao;
import com.sisgfip.repository.ContaRepository;
import com.sisgfip.repository.MovimentacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de negócio para Movimentações.
 *
 * Responsabilidade central: garantir consistência entre
 * a movimentação registrada e o saldo da conta.
 * Ambas devem ser salvas na MESMA transação (@Transactional).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ContaRepository contaRepository;

    /**
     * Registra uma movimentação e atualiza o saldo da conta atomicamente.
     *
     * @Transactional garante que se qualquer passo falhar,
     * tudo é revertido — sem saldo desincronizado.
     */
    @Transactional
    public MovimentacaoResponse registrar(MovimentacaoRequest request) {
        Conta conta = contaRepository.findById(request.contaId())
            .orElseThrow(() -> ResourceNotFoundException.conta(request.contaId()));

        if (!conta.isAtiva()) {
            throw new BusinessException("Não é possível registrar movimentação em conta inativa");
        }

        // Atualiza o saldo na entidade Conta
        if (request.tipo() == TipoMovimentacao.RECEITA) {
            conta.creditar(request.valor());
        } else {
            conta.debitar(request.valor());
        }

        // Persiste a movimentação
        Movimentacao mov = Movimentacao.builder()
            .tipo(request.tipo())
            .valor(request.valor())
            .descricao(request.descricao())
            .categoria(request.categoria())
            .conta(conta)
            .build();

        mov = movimentacaoRepository.save(mov);

        // Persiste o saldo atualizado na conta
        contaRepository.save(conta);

        log.info("Movimentação registrada: id={}, tipo={}, valor={}, conta={}",
            mov.getId(), mov.getTipo(), mov.getValor(), conta.getId());

        return toResponse(mov);
    }

    @Transactional(readOnly = true)
    public MovimentacaoResponse buscarPorId(Long id) {
        return toResponse(findOrThrow(id));
    }

    /** Lista o extrato paginado de uma conta */
    @Transactional(readOnly = true)
    public Page<MovimentacaoResponse> listarPorConta(Long contaId, Pageable pageable) {
        return movimentacaoRepository
            .findByContaIdOrderByDataHoraDesc(contaId, pageable)
            .map(this::toResponse);
    }

    /** Lista movimentações de uma conta filtradas por tipo */
    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listarPorTipo(Long contaId, TipoMovimentacao tipo) {
        return movimentacaoRepository
            .findByContaIdAndTipoOrderByDataHoraDesc(contaId, tipo)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    /** Lista movimentações em um período (início e fim no formato ISO-8601) */
    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listarPorPeriodo(
            Long contaId, LocalDateTime inicio, LocalDateTime fim) {
        return movimentacaoRepository
            .findByContaIdAndPeriodo(contaId, inicio, fim)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * Remove uma movimentação e reverte o saldo da conta.
     * Regra: só é possível deletar movimentações recentes (negociável com o produto).
     */
    @Transactional
    public void deletar(Long id) {
        Movimentacao mov = findOrThrow(id);
        Conta conta = mov.getConta();

        // Reverte o efeito no saldo
        if (mov.getTipo() == TipoMovimentacao.RECEITA) {
            conta.debitar(mov.getValor());
        } else {
            conta.creditar(mov.getValor());
        }

        contaRepository.save(conta);
        movimentacaoRepository.delete(mov);
        log.info("Movimentação deletada: id={}, saldo conta revertido", id);
    }

    private Movimentacao findOrThrow(Long id) {
        return movimentacaoRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.movimentacao(id));
    }

    public MovimentacaoResponse toResponse(Movimentacao m) {
        return new MovimentacaoResponse(
            m.getId(),
            m.getTipo(),
            m.getValor(),
            m.getDescricao(),
            m.getCategoria(),
            m.getDataHora(),
            m.getConta().getId(),
            m.getConta().getNome()
        );
    }
}
