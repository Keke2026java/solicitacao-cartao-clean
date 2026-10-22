package br.edu.fiap.cartoes.application.usecase;

import br.edu.fiap.cartoes.application.model.SolicitacaoEntrada;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.domain.model.Usuario;
import br.edu.fiap.cartoes.domain.exception.RegraNegocioException;
import br.edu.fiap.cartoes.application.ports.CartaoRepository;
import br.edu.fiap.cartoes.application.ports.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.application.ports.TipoCartaoRepository;
import br.edu.fiap.cartoes.application.ports.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/** Estrutura de teste do grupo; não contém teste resolvido nem @Test de exemplo. */
class SolicitacaoUseCaseTest {
    // TODO: declarar os cenários e implementar as verificações descritas no README.
    private final UsuarioRepository usuarios= mock(UsuarioRepository.class);
    private final TipoCartaoRepository tipos = mock(TipoCartaoRepository.class);
    private final SolicitacaoCartaoRepository solicitacoes=mock(SolicitacaoCartaoRepository.class);
    private final CartaoRepository cartoes=mock(CartaoRepository.class);
    private final SolicitacaoUseCase useCase=new SolicitacaoUseCase(usuarios,tipos,solicitacoes,cartoes);

    @Test
    void naoDeveCriarSolicitacaoPendenteDuplicada(){
        Usuario usuario=novoUsuario();
        TipoCartao tipo=novoTipo();

        when(usuarios.buscarPorIdComBloqueio(1L))
                .thenReturn(Optional.of(usuario));
        when(tipos.buscarPorId(1L))
                .thenReturn(Optional.of(tipo));
        when(solicitacoes.existePendente(1L,1L))
                .thenReturn(true);

        SolicitacaoEntrada request=new SolicitacaoEntrada(1L);
        assertThrows(RegraNegocioException.class,()->useCase.criar(1L,request));
        verify(solicitacoes,never()).salvar(any(SolicitacaoCartao.class));
    }

    @Test
    void naoDeveEmitirCartaoDeSolicitacaoCancelada(){
        SolicitacaoCartao solicitacao = new SolicitacaoCartao(novoUsuario(),novoTipo());
        solicitacao.setId(10L);
        solicitacao.cancelar();

        when(solicitacoes.buscarPorIdETitularComBloqueio(10L,1L))
                .thenReturn(Optional.of(solicitacao));
        when(cartoes.existePorSolicitacao(10L))
                .thenReturn(false);
        assertThrows(RegraNegocioException.class,()->useCase.emitir(1L,10L));
        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());

    }
    private Usuario novoUsuario(){
        Usuario usuario=new Usuario("Ana","ana@example.com","hash-ficticio-para-teste");
        usuario.setId(1L);
        return usuario;
    }
    private TipoCartao novoTipo(){
        TipoCartao tipo= new TipoCartao( "ESSENCIAL","Cartão para o dia a dia",true);
        tipo.setId(1L);
        return tipo;
    }
}
