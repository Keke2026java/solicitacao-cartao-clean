package br.edu.fiap.cartoes.application.ports;

import br.edu.fiap.cartoes.application.model.TokenResultado;
import br.edu.fiap.cartoes.domain.model.Usuario;

public interface EmissorToken {

    TokenResultado emitir(Usuario usuario);
}
