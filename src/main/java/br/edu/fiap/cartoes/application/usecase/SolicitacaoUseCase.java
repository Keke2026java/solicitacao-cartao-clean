package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.application.model.CartaoResultado;
import br.edu.fiap.cartoes.application.model.SolicitacaoEntrada;
import br.edu.fiap.cartoes.application.model.SolicitacaoResultado;
import br.edu.fiap.cartoes.domain.model.*;
import br.edu.fiap.cartoes.application.exception.RecursoNaoEncontradoException;
import br.edu.fiap.cartoes.domain.exception.RegraNegocioException;
import br.edu.fiap.cartoes.application.ports.CartaoRepository;
import br.edu.fiap.cartoes.application.ports.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.application.ports.TipoCartaoRepository;
import br.edu.fiap.cartoes.application.ports.UsuarioRepository;

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
@Transactional
public class SolicitacaoUseCase {
    private final UsuarioRepository usuarios;
    private final TipoCartaoRepository tipos;
    private final SolicitacaoCartaoRepository solicitacoes;
    private final CartaoRepository cartoes;

    @Inject
    public SolicitacaoUseCase(UsuarioRepository usuarios, TipoCartaoRepository tipos, SolicitacaoCartaoRepository solicitacoes, CartaoRepository cartoes) {
        this.usuarios = usuarios;
        this.tipos = tipos;
        this.solicitacoes = solicitacoes;
        this.cartoes = cartoes;
    }
    /** Buscar titular e tipo por JPA; tipo indisponível ou pedido pendente duplicado gera 409. */
    public SolicitacaoResultado criar(Long usuarioId, SolicitacaoEntrada request) {
        Usuario usuario=usuarios.buscarPorIdComBloqueio(usuarioId)
                .orElseThrow(()->new RecursoNaoEncontradoException("Usuário não encontrado"));
        TipoCartao tipo= tipos.buscarPorId(request.tipoCartaoId())
                .orElseThrow(()->new RecursoNaoEncontradoException("Tipo de cartão não encontrado"));
        if(!tipo.isDisponivel()){
            throw new RegraNegocioException("Tipo de cartão indisponível");
        }
        if(solicitacoes.existePendente(usuarioId,tipo.getId())){
            throw new RegraNegocioException("Já existe solicitação pendente para este tipo de cartão");
        }
        SolicitacaoCartao solicitacao=new SolicitacaoCartao(usuario,tipo);
        solicitacoes.salvar(solicitacao);
        return montarResposta(solicitacao);
    }


    /** JPQL própria com filtro de titular e status opcional, nunca listar todos os usuários. */
    public List<SolicitacaoResultado>listar(Long usuarioId, StatusSolicitacao status){
        List<SolicitacaoCartao> encontradas=solicitacoes.listarPorTitularEStatus(usuarioId,status);
        List<SolicitacaoResultado>respostas=new ArrayList<>();
        for(SolicitacaoCartao solicitacao:encontradas){
            respostas.add(montarResposta(solicitacao));
        }
        return  respostas;
    }

    /** Buscar por id E titular; retornar 404 para pedido de outro usuário. */
    public SolicitacaoResultado buscar(Long usuarioId, Long id) {
        SolicitacaoCartao solicitacao = solicitacoes.buscarPorIdETitular(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada"));
        return montarResposta(solicitacao);
        // TODO: implementar sem colocar SQL ou regras no controller.
    }

    /** Em transação, buscar o próprio pedido e chamar cancelar; estado diferente de PENDENTE gera 409. */
    public SolicitacaoResultado cancelar(Long usuarioId, Long id) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        SolicitacaoCartao solicitacao = solicitacoes.buscarPorIdETitularComBloqueio(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada"));
        solicitacao.cancelar();
        return montarResposta(solicitacao);
    }


    /** Em uma transação, bloquear o pedido para escrita, conferir titular/estado, criar cartão e marcar EMITIDA. */
    public CartaoResultado emitir(Long usuarioId, Long id) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        SolicitacaoCartao solicitacao = solicitacoes.buscarPorIdETitularComBloqueio(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada"));
        if (cartoes.existePorSolicitacao(solicitacao.getId())) {
            throw new RegraNegocioException("Cartão já emitido para esta solicitação");
        }
        solicitacao.marcarEmitida();

        Cartao cartao = new Cartao(solicitacao);
        cartoes.salvar(cartao);

        TipoCartao tipo = solicitacao.getTipoCartao();

        return new CartaoResultado(cartao.getId(), solicitacao.getId(), tipo.getId(), tipo.getNome(), cartao.getEmitidoEm(), cartao.getStatus());
    }

        private SolicitacaoResultado montarResposta(SolicitacaoCartao solicitacao){
            return new SolicitacaoResultado(solicitacao.getId(), solicitacao.getUsuario().getId(),
                    solicitacao.getTipoCartao().getId(),solicitacao.getTipoCartao().getNome(),
                    solicitacao.getSolicitadaEm(), solicitacao.getStatus());

        }
    }

