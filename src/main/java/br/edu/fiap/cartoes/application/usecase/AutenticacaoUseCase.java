package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.application.model.LoginEntrada;
import br.edu.fiap.cartoes.application.model.TokenResultado;
import br.edu.fiap.cartoes.application.ports.EmissorToken;
import br.edu.fiap.cartoes.application.ports.HasherSenha;
import br.edu.fiap.cartoes.domain.model.Usuario;
import br.edu.fiap.cartoes.application.exception.CredenciaisInvalidasException;
import br.edu.fiap.cartoes.application.ports.UsuarioRepository;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Locale;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class AutenticacaoUseCase {
    private final UsuarioRepository usuarios;
    private final HasherSenha senhas;
    private final EmissorToken tokens;
    // TODO: implementar sem colocar SQL ou regras no controller.

    @Inject
    public AutenticacaoUseCase(UsuarioRepository usuarios,HasherSenha senhas, EmissorToken tokens) {
        this.usuarios = usuarios;
        this.senhas = senhas;
        this.tokens = tokens;
    }
    /** Buscar e-mail normalizado, conferir BCrypt, negar genericamente ou emitir JWT. */
    @Transactional
    public TokenResultado login(LoginEntrada request) {
        String email=request.email().trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarios.buscarPorEmail(email).
                orElseThrow(CredenciaisInvalidasException::new);
        if(!senhas.confere(request.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return  tokens.emitir(usuario);


    }

}
