package br.com.mecaniQA.api.mapper.JDBC;

import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.StatusOrdemServico;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrdemServicoResultSetMapper {

    private OrdemServicoResultSetMapper() {}

    public static OrdemServico map(ResultSet rs) throws SQLException {
        return OrdemServico.builder()
                .codigoUnico(rs.getLong("codigo_unico"))
                .descricaoProblema(rs.getString("descricao_problema"))
                .status(StatusOrdemServico.valueOf(rs.getString("status")))
                .valorTotal(rs.getDouble("valor_total"))
                .dataAbertura(rs.getTimestamp("data_abertura") != null
                        ? rs.getTimestamp("data_abertura").toLocalDateTime()
                        : null)
                .dataUltimaAtualizacao(rs.getTimestamp("data_ultima_atualizacao") != null
                        ? rs.getTimestamp("data_ultima_atualizacao").toLocalDateTime()
                        : null)
                .servicos(new ArrayList<>())
                .pedidosPecas(new ArrayList<>())
                .build();
    }
}