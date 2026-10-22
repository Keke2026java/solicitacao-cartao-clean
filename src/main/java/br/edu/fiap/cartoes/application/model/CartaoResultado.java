package br.edu.fiap.cartoes.application.model;

import br.edu.fiap.cartoes.domain.model.StatusCartao;

import java.time.Instant;

public record CartaoResultado(Long id,
                              Long solicitacaoId,
                              Long tipoCartaoId,
                              String tipoCartaoNome,
                              Instant emitidoEm,
                              StatusCartao status) {
}
