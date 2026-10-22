package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.application.model.UsuarioEntrada;
import br.edu.fiap.cartoes.application.model.UsuarioResultado;
import br.edu.fiap.cartoes.application.ports.HasherSenha;
import br.edu.fiap.cartoes.domain.model.Usuario;
import br.edu.fiap.cartoes.domain.exception.RegraNegocioException;
import br.edu.fiap.cartoes.application.ports.UsuarioRepository;
import br.edu.fiap.cartoes.presenter.dto.UsuarioRequest;
import br.edu.fiap.cartoes.presenter.dto.UsuarioResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Locale;
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
    private final HasherSenha senhas;

    @Inject
    public UsuarioUseCase(UsuarioRepository usuarios, HasherSenha senhas) {
        this.usuarios = usuarios;
        this.senhas = senhas;
    }

    @Transactional
    public UsuarioResultado cadastrar(UsuarioEntrada request) {
        // TODO: implementar sem colocar SQL ou regras no controller.
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (usuarios.buscarPorEmail(email).isPresent()) {
            throw new RegraNegocioException("E-mail já cadastrado. ");
        }
        String hash = senhas.criarHash(request.senha());
        Usuario novoUsuario = new Usuario(request.nome().trim(), email, hash);

        Usuario usuarioSalvo= usuarios.salvar(novoUsuario);
        return new UsuarioResultado(usuarioSalvo.getId(),
                usuarioSalvo.getNome(), usuarioSalvo.getEmail(),
                usuarioSalvo.getCriadoEm());
    }
}