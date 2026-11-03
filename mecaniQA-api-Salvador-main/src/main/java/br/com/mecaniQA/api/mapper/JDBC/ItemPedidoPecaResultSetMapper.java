package br.com.mecaniQA.api.mapper.JDBC;

import br.com.mecaniQA.api.model.ItemPedidoPeca;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemPedidoPecaResultSetMapper {

    private ItemPedidoPecaResultSetMapper() {}

    public static ItemPedidoPeca map(ResultSet rs, PedidoPecas pedido) throws SQLException {
        Peca peca = new Peca(null, null, null, 0, 0.0, 0.0, null);
        peca.setCodigoUnico(rs.getLong("peca_id"));

        return new ItemPedidoPeca(
                pedido,
                peca,
                rs.getInt("quantidade"),
                rs.getDouble("preco_unitario")
        );
    }
}