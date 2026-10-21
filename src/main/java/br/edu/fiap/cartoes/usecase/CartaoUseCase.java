package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.entity.Cartao;
import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.TipoCartao;
import br.edu.fiap.cartoes.repository.CartaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import br.edu.fiap.cartoes.dto.*;
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
    public List<CartaoResponse> listar(Long usuarioId) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        List<Cartao> encontrados= cartoes.listarPorTitular(usuarioId);
        List<CartaoResponse> respostas = new ArrayList<>();
        for(Cartao cartao: encontrados){
            SolicitacaoCartao solicitacao=cartao.getSolicitacao();

            TipoCartao tipo= solicitacao.getTipoCartao();

            CartaoResponse resposta = new CartaoResponse(cartao.getId(),
                    solicitacao.getId(), tipo.getId(), tipo.getNome(),
                    cartao.getEmitidoEm(),
                    cartao.getStatus());
            respostas.add(resposta);
        }
        return respostas;
    }
}
