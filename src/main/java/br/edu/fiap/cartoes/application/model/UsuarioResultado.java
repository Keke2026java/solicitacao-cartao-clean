package br.edu.fiap.cartoes.application.model;

import java.time.Instant;

public record UsuarioResultado(Long id,
                               String nome,
                               String email,
                               Instant criadoEm) {
}
