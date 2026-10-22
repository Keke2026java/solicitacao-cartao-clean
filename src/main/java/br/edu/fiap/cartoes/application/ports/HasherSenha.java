package br.edu.fiap.cartoes.application.ports;

public interface HasherSenha {
    String criarHash(String senha);
    boolean confere(String senha, String hash);
}
