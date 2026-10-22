package br.edu.fiap.cartoes.infrastructure.repository.rest;
import br.edu.fiap.cartoes.application.ports.ConsultaTiposDisponiveis;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador JDBC: use AgroalDataSource, PreparedStatement e try-with-resources.
 * Não chamar controller nem gerar token nesta camada. Esta é a única implementação JDBC obrigatória.
 */
@ApplicationScoped
public class TipoCartaoRepositoryJdbc implements ConsultaTiposDisponiveis {
    // TODO: implementar a interface depois de declarar suas assinaturas.

    private final AgroalDataSource dataSource;

    @Inject
    public TipoCartaoRepositoryJdbc(AgroalDataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public List<TipoCartao>listarDisponiveis(){
        String sql="""
                SELECT id,nome, 
                descricao,disponivel FROM tipos_cartao WHERE disponivel=? ORDER BY nome""";

        List<TipoCartao> tipos = new ArrayList<>();
        try(Connection conexao = dataSource.getConnection();
            PreparedStatement consulta = conexao.prepareStatement(sql)){
            consulta.setBoolean(1,true);

            try(ResultSet resultado = consulta.executeQuery()){
                while(resultado.next()){
                    TipoCartao tipo = (new TipoCartao(

                            resultado.getString("nome"),
                            resultado.getString("descricao"),
                            resultado.getBoolean("disponivel")
                    ));
                    tipo.setId(resultado.getLong("id"));
                    tipos.add(tipo);
                }
            }
            return tipos;
        } catch (SQLException erro){
            throw new IllegalStateException("Falha ao consultar tipos de cartão disponíveis",erro);
        }
    }
}

