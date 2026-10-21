package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.exception.CredenciaisInvalidasException;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import br.edu.fiap.cartoes.security.JwtService;
import br.edu.fiap.cartoes.security.SenhaBCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Locale;

import br.edu.fiap.cartoes.dto.*;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;
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
    private final SenhaBCrypt senhas;
    private final JwtService tokens;
    // TODO: implementar sem colocar SQL ou regras no controller.

    @Inject
    public AutenticacaoUseCase(UsuarioRepository usuarios,SenhaBCrypt senhas, JwtService tokens) {
        this.usuarios = usuarios;
        this.senhas = senhas;
        this.tokens = tokens;
    }
    /** Buscar e-mail normalizado, conferir BCrypt, negar genericamente ou emitir JWT. */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        String email=request.email().trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarios.buscarPorEmail(email).
                orElseThrow(CredenciaisInvalidasException::new);
        if(!senhas.confere(request.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return  tokens.emitir(usuario);


    }

}
