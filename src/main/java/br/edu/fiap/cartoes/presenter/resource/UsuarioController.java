package br.edu.fiap.cartoes.presenter.resource;
import br.edu.fiap.cartoes.application.model.UsuarioEntrada;
import br.edu.fiap.cartoes.application.model.UsuarioResultado;
import br.edu.fiap.cartoes.application.usecase.UsuarioUseCase;
import br.edu.fiap.cartoes.presenter.dto.ErroResponse;
import br.edu.fiap.cartoes.presenter.dto.UsuarioRequest;
import br.edu.fiap.cartoes.presenter.dto.UsuarioResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.*;
import org.eclipse.microprofile.openapi.annotations.media.*;

/**
 * Camada controller pronta: traduz HTTP, valida o contrato e delega à aplicação.
 * Não escrever consultas ou decisões de estado aqui. Na migração Clean, esta classe
 * será um adaptador de entrada; preservar as rotas e seus contratos.
 */
@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="Cadastro")
@PermitAll
public class UsuarioController {
    @Inject
    UsuarioUseCase usuarios;

    /** Pública. Validar entrada, normalizar e-mail, verificar unicidade, gerar BCrypt e persistir por JPA. Retornar somente dados públicos. */
    @POST
    @Operation(summary="Cadastrar usuário", description="Pública. Validar entrada, normalizar e-mail, verificar unicidade, gerar BCrypt e persistir por JPA. Retornar somente dados públicos.")
    @APIResponses({
        @APIResponse(responseCode="201",description="Operação concluída",content=@Content(schema=@Schema(implementation= UsuarioResponse.class))),
        @APIResponse(responseCode="400",description="Entrada inválida",content=@Content(schema=@Schema(implementation= ErroResponse.class))),
        @APIResponse(responseCode="409",description="Duplicidade ou estado incompatível"),
        @APIResponse(responseCode="501",description="Casca ainda não implementada",content=@Content(schema=@Schema(implementation=ErroResponse.class)))
    })
    public Response cadastrar(@Valid @NotNull UsuarioRequest request) {
        UsuarioEntrada entrada = new UsuarioEntrada(request.nome(),
                request.email(), request.senha());
        UsuarioResultado resultado = usuarios.cadastrar(entrada);

        UsuarioResponse resposta = new UsuarioResponse(resultado.id(), resultado.nome(),
                resultado.email(), resultado.criadoEm());
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }
}
