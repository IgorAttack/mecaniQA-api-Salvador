package br.com.mecaniQA.api.mapper.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import br.com.mecaniQA.api.model.Peca;

public class PecaResultSetMapper {
    private PecaResultSetMapper() {}

public static Peca map(ResultSet rs) throws SQLException {
    Peca peca = new Peca (
            rs.getString ("nome"),
            null,
            null,
            0,
            0.0,
            rs.getDouble("preco"),
            null
    );
    peca.setCodigoUnico(rs.getLong("codigo_unico"));
    if (rs.getTimestamp("data_cadastro") != null) {
        peca.setDataCadastro(rs.getTimestamp("data_cadastro").toLocalDateTime());
    }
    if(rs.getTimestamp("data_ultima_atualizacao") != null) {
        peca.setDataUltimaAtualizacao(rs.getTimestamp("data_ultima_atualizacao").toLocalDateTime());
    }
    return peca;
}
}
