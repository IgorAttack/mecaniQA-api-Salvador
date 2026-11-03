package br.com.mecaniQA.api.mapper.JDBC;

import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedidoPecas;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PedidoPecasResultSetMapper {

    private PedidoPecasResultSetMapper() {}

    public static PedidoPecas map(ResultSet rs) throws SQLException {
        PedidoPecas pedido = new PedidoPecas(
                rs.getString("fornecedor"),
                StatusPedidoPecas.valueOf(rs.getString("status")),
                rs.getDouble("valor_total")
        );
        pedido.setCodigoUnico(rs.getLong("codigo_unico"));

        if (rs.getTimestamp("data_pedido") != null) {
            pedido.setDataPedido(rs.getTimestamp("data_pedido").toLocalDateTime());
        }
        if (rs.getTimestamp("data_ultima_atualizacao") != null) {
            pedido.setDataUltimaAtualizacao(rs.getTimestamp("data_ultima_atualizacao").toLocalDateTime());
        }
        return pedido;
    }
}