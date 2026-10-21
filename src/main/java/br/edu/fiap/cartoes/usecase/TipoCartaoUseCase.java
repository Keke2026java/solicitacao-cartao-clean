package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.entity.TipoCartao;
import br.edu.fiap.cartoes.repository.ConsultaTiposDisponiveis;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import br.edu.fiap.cartoes.dto.*;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;
import jakarta.inject.Inject;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class TipoCartaoUseCase {

    private  final ConsultaTiposDisponiveis consulta;

    @Inject
    public TipoCartaoUseCase(ConsultaTiposDisponiveis consulta) {
        this.consulta = consulta;
    }

    /** Delegar à única consulta JDBC e mapear a resposta. */
    public List<TipoCartaoResponse> listarDisponiveis() {
        // TODO: implementar sem colocar SQL ou regras no controller.
        List<TipoCartao> tipos = consulta.listarDisponiveis();
        List<TipoCartaoResponse> respostas = new ArrayList<>();
        for(TipoCartao tipo: tipos){
            TipoCartaoResponse resposta = new TipoCartaoResponse(tipo.getId(),
                    tipo.getNome(), tipo.getDescricao(),
                    tipo.isDisponivel());
            respostas.add(resposta);
        }
        return respostas;
    }
}
