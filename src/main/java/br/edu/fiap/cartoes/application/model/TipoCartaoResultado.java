package br.edu.fiap.cartoes.application.model;

public record TipoCartaoResultado(Long id,
                                  String nome,
                                  String descricao,
                                  boolean disponivel) {
}
