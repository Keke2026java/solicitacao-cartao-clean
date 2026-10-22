package br.edu.fiap.cartoes.application.model;

public record TokenResultado(String acessToken, String tokenType, long expireIn,long expireAt) {
}
