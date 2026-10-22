package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.application.model.TipoCartaoResultado;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.application.ports.ConsultaTiposDisponiveis;
import br.edu.fiap.cartoes.presenter.dto.TipoCartaoResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class TipoCartaoUseCase {

    private final ConsultaTiposDisponiveis consulta;

    @Inject
    public TipoCartaoUseCase(ConsultaTiposDisponiveis consulta) {
        this.consulta = consulta;
    }

    /**
     * Delegar à única consulta JDBC e mapear a resposta.
     */
    public List<TipoCartaoResultado> listarDisponiveis() {
        // TODO: implementar sem colocar SQL ou regras no controller.
        // List<TipoCartao> tipos = consulta.listarDisponiveis();
        List<TipoCartaoResultado> resultados= new ArrayList<>();
        for (TipoCartao tipo : consulta.listarDisponiveis()) {
            resultados.add(new TipoCartaoResultado(tipo.getId(), tipo.getNome(),
                    tipo.getDescricao(), tipo.isDisponivel()));
        }
        return resultados;
    }
}
