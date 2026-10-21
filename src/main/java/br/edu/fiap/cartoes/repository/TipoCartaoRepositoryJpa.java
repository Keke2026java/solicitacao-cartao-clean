package br.edu.fiap.cartoes.repository;
import br.edu.fiap.cartoes.entity.TipoCartao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.Optional;

/** Adaptador JPA a implementar: buscar o tipo por ID ao criar uma solicitação. */
@ApplicationScoped
public class TipoCartaoRepositoryJpa implements TipoCartaoRepository {
    private final EntityManager entityManager;
    // TODO: implementar a busca com EntityManager, sem chamar o adaptador JDBC.

    @Inject
    public TipoCartaoRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Override
    public Optional<TipoCartao>buscarPorId(Long id) {
        TipoCartao tipo = entityManager.find(TipoCartao.class, id);
        return Optional.ofNullable(tipo);
    }
}
