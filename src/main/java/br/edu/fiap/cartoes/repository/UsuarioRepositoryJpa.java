package br.edu.fiap.cartoes.repository;
import br.edu.fiap.cartoes.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.hibernate.LockMode;

import java.util.Optional;

/**
 * Adaptador JPA: injete EntityManager, escreva JPQL parametrizada e retorne entidades.
 * Não chamar controller nem gerar token nesta camada. Gravações participam da transação aberta pelo use case.
 */
@ApplicationScoped
public class UsuarioRepositoryJpa implements UsuarioRepository {
    // TODO: implementar a interface depois de declarar suas assinaturas.

    private final EntityManager entityManager;

    @Inject
    public UsuarioRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        entityManager.persist(usuario);
        return usuario;
    }
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return entityManager.createQuery("SELECT u FROM Usuario u WHERE u.email = :email"
                        , Usuario.class)
                .setParameter("email", email)
                .getResultList().stream()
                .findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        Usuario usuario=entityManager.find(Usuario.class, id);
        return Optional.ofNullable(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorIdComBloqueio(Long id) {
        Usuario usuario=entityManager.find(Usuario.class, id, LockMode.PESSIMISTIC_WRITE);
        return Optional.ofNullable(usuario);
    }
}
