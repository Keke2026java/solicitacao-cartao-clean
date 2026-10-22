package br.edu.fiap.cartoes.application.model;

import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;

import java.time.Instant;

public record SolicitacaoResultado(Long id,
                                   Long UsuarioId,
                                   Long TipoCartaoId,
                                   String tipoCartaoNome,
                                   Instant solicitadoEm,
                                   StatusSolicitacao status) {
}
