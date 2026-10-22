package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.application.model.CartaoResultado;
import br.edu.fiap.cartoes.domain.model.Cartao;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.application.ports.CartaoRepository;
import br.edu.fiap.cartoes.presenter.dto.CartaoResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class CartaoUseCase {

    private final CartaoRepository cartoes;

    @Inject
    public CartaoUseCase(CartaoRepository cartoes) {
        this.cartoes = cartoes;
    }
    /** JPQL com relacionamento cartão -> solicitação -> usuário e tipo; somente cartões próprios. */
    @Transactional
    public List<CartaoResultado> listar(Long usuarioId) {
        // TODO: implementar sem colocar SQL ou regras no controller.

        List<CartaoResultado> resultados = new ArrayList<>();
        for(Cartao cartao: cartoes.listarPorTitular(usuarioId)){
            SolicitacaoCartao solicitacao=cartao.getSolicitacao();

            TipoCartao tipo= solicitacao.getTipoCartao();

            resultados.add(new CartaoResultado(cartao.getId(),
                    solicitacao.getId(), tipo.getId(), tipo.getNome(),
                    cartao.getEmitidoEm(),cartao.getStatus()));
        }
        return resultados;
    }
}
