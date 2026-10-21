package br.edu.fiap.cartoes.entity;

import br.edu.fiap.cartoes.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Estrutura de teste do grupo; não contém teste resolvido nem @Test de exemplo. */
class SolicitacaoCartaoTest {
    // TODO: declarar os cenários e implementar as verificações descritas no README.

    @Test
    void deveCancelarSolicitacaoPendente() {
        SolicitacaoCartao solicitacao = novaSolicitacao();
        solicitacao.cancelar();
        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());
    }

    @Test
    void naoDeveEmitirSolicitacaoCancelada() {
        SolicitacaoCartao solicitacao = novaSolicitacao();
        solicitacao.cancelar();
        assertThrows(RegraNegocioException.class, () -> solicitacao.marcarEmitida());
        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());
    }

    private SolicitacaoCartao novaSolicitacao() {
        Usuario usuario = new Usuario("Kenia", "keniamaria@gmail.com", "89890909");

        TipoCartao tipo = new TipoCartao("Visa Black", "Cartão Black", true);
        return new SolicitacaoCartao(usuario, tipo);
    }
}

