package br.edu.fiap.cartoes.infrastructure.security;
import br.edu.fiap.cartoes.application.ports.HasherSenha;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import java.nio.charset.StandardCharsets;

/**
 * Adaptador de senha a implementar com BcryptUtil do Quarkus.
 * BCrypt é hash com salt, não criptografia reversível. O custo é logarítmico (2^custo),
 * não o número literal de repetições. Validar limite de 72 bytes UTF-8 e não logar senhas.
 */
@ApplicationScoped
public class SenhaBCrypt implements HasherSenha {
    @ConfigProperty(name = "security.password.bcrypt-cost")
    int custo;

    /**
     * TODO usar BcryptUtil.bcryptHash com salt aleatório gerado pela biblioteca.
     */
    @Override
    public String criarHash(String senha) {
        if (senha == null || senha.isBlank() || senha.length() < 8 || senha.length() > 72) {
            throw new BadRequestException("Senha inválida. A senha deve ter entre 8 e 72 caracteres. ");
        }
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("Senha inválida. A senha deve ter no máximo 72 bytes em UTF-8. ");
        }
        return BcryptUtil.bcryptHash(senha, custo);
    }

    /**
     * TODO usar BcryptUtil.matches sem descriptografar, rehashar ou comparar Strings de hash.
     */

@Override
    public boolean confere(String senha, String hash) {
        if (senha == null || hash == null || hash.isBlank()) {
            return false;
        }
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            return false;
        }
        return BcryptUtil.matches(senha, hash);
    }
    }

