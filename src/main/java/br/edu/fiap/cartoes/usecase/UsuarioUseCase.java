package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.exception.RegraNegocioException;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import br.edu.fiap.cartoes.security.SenhaBCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Locale;
import br.edu.fiap.cartoes.dto.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class UsuarioUseCase {
    /**
     * Normalizar e-mail, rejeitar duplicado, gerar hash BCrypt, persistir e mapear sem senha.
     */

    private final UsuarioRepository usuarios;
    private final SenhaBCrypt senhas;

    @Inject
    public UsuarioUseCase(UsuarioRepository usuarios, SenhaBCrypt senhas) {
        this.usuarios = usuarios;
        this.senhas = senhas;
    }

    @Transactional
    public UsuarioResponse cadastrar(@NotNull @Valid
                                     UsuarioRequest request) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (usuarios.buscarPorEmail(email).isPresent()) {
            throw new RegraNegocioException("E-mail já cadastrado. ");
        }
        String hash = senhas.criarHash(request.senha());
        Usuario novoUsuario = new Usuario(request.nome().trim(), email, hash);

        Usuario usuarioSalvo= usuarios.salvar(novoUsuario);
        return new UsuarioResponse(usuarioSalvo.getId(),
                usuarioSalvo.getNome(), usuarioSalvo.getEmail(),
                usuarioSalvo.getCriadoEm());
    }
}