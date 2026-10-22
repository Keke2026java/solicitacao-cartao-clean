package br.edu.fiap.cartoes.presenter.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/** Estrutura de teste do grupo; não contém teste resolvido nem @Test de exemplo. */
@QuarkusTest
class SegurancaIntegracaoTest {
    // TODO: declarar os cenários e implementar as verificações descritas no README.
    @Test
    void deveRecusarConsultaSemToken()
    {
        given().when().get("/api/tipos-cartao").then().statusCode(401);
    }

}
