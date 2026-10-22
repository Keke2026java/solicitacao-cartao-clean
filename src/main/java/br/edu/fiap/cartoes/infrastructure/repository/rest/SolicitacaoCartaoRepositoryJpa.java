package br.edu.fiap.cartoes.infrastructure.repository.rest;
import br.edu.fiap.cartoes.application.ports.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA: injete EntityManager, escreva JPQL parametrizada e retorne entidades.
 * Não chamar controller nem gerar token nesta camada. Gravações participam da transação aberta pelo use case.
 */
@ApplicationScoped
public class SolicitacaoCartaoRepositoryJpa implements SolicitacaoCartaoRepository {

    private final EntityManager entityManager;
    // TODO: implementar a interface depois de declarar suas assinaturas.
    @Inject
    public SolicitacaoCartaoRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Override
    public SolicitacaoCartao salvar(SolicitacaoCartao solicitacao) {
        entityManager.persist(solicitacao);
        return solicitacao;
    }
    @Override
    public Optional<SolicitacaoCartao> buscarPorIdETitular(Long id,Long UsuarioId) {
        return entityManager.createQuery("SELECT s FROM SolicitacaoCartao s JOIN FETCH s.tipoCartao WHERE s.id = :id AND s.usuario.id = :usuarioId", SolicitacaoCartao.class)
                .setParameter("id", id)
                .setParameter("usuarioId", UsuarioId)
                .getResultList()
                .stream().findFirst();
    }
    @Override
    public Optional<SolicitacaoCartao>buscarPorIdETitularComBloqueio(Long id,Long UsuarioId) {
        return entityManager.createQuery("SELECT s FROM SolicitacaoCartao s JOIN FETCH s.tipoCartao WHERE s.id = :id AND s.usuario.id = :usuarioId", SolicitacaoCartao.class)
                .setParameter("id", id)
                .setParameter("usuarioId", UsuarioId)
                .setLockMode(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public List<SolicitacaoCartao> listarPorTitularEStatus(Long usuarioId, StatusSolicitacao status) {
        String jpql = """
                SELECT s FROM SolicitacaoCartao s JOIN FETCH s.tipoCartao WHERE s.usuario.id = :usuarioId""";
        if (status != null) {
            jpql += " AND s.status = :status";
        }
        jpql += " ORDER BY s.solicitadaEm, s.id";
        TypedQuery<SolicitacaoCartao> consulta = entityManager.createQuery(jpql, SolicitacaoCartao.class);
        consulta.setParameter("usuarioId", usuarioId);
        if (status != null) {
            consulta.setParameter("status", status);
    }
        return consulta.getResultList();
    }

   @Override
public boolean existePendente(Long usuarioId,Long tipoCartaoId) {
    String jpql = " SELECT COUNT(s) FROM SolicitacaoCartao s WHERE s.usuario.id = :usuarioId AND s.tipoCartao.id = :tipoCartaoId " +
            " AND s.status = :status";
    Long quantidade = entityManager.createQuery("""
            SELECT COUNT(s) FROM SolicitacaoCartao s WHERE s.usuario.id = :usuarioId AND s.tipoCartao.id = :tipoCartaoId
             AND s.status = :status"""
            , Long.class)
            .setParameter("usuarioId", usuarioId)
            .setParameter("tipoCartaoId", tipoCartaoId)
            .setParameter("status", StatusSolicitacao.PENDENTE)
            .getSingleResult();
    return quantidade > 0;
}
}