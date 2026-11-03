package br.com.mecaniQA.api.mapper.JDBC;

import br.com.mecaniQA.api.model.Servico;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicoResultSetMapper {

    private ServicoResultSetMapper() {}

    public static Servico map(ResultSet rs) throws SQLException {
        Servico servico = new Servico(
                rs.getString("nome"),
                0,
                rs.getDouble("preco")
        );
        servico.setCodigoUnico(rs.getLong("codigo_unico"));

        if (rs.getTimestamp("data_criacao") != null) {
            servico.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        }
        if (rs.getTimestamp("data_ultima_atualizacao") != null) {
            servico.setDataUltimaAtualizacao(rs.getTimestamp("data_ultima_atualizacao").toLocalDateTime());
        }
        return servico;
    }
}