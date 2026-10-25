package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.ItemPedidoPeca;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedidoPecas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class PedidoPecasRepository extends AbstractJdbcRepository<PedidoPecas, Long> {

    private static PedidoPecasRepository instance;

    private PedidoPecasRepository() {}

    public static PedidoPecasRepository getInstance() {
        if (instance == null) {
            instance = new PedidoPecasRepository();
        }
        return instance;
    }

    /**
     * Salva o pedido e seus itens dentro de uma única transação.
     * O algoritmo específico da entidade permanece no repositório, enquanto
     * as consultas simples são centralizadas no Template Method.
     */
    @Override
    public PedidoPecas salvar(PedidoPecas pedido) {
        return executarEmTransacao(connection -> {
            if (pedido.getCodigoUnico() == null) {
                pedido.setDataPedido(LocalDateTime.now());
                if (pedido.getStatus() == null) {
                    pedido.setStatus(StatusPedidoPecas.ORCANDO);
                }
                if (pedido.getValorTotal() == null) {
                    pedido.setValorTotal(0.0);
                }

                executarSalvar(
                        connection,
                        "INSERT INTO pedidos (fornecedor, status, valor_total, data_pedido) VALUES (?, ?, ?, ?)",
                        pedido,
                        stmt -> {
                            stmt.setString(1, pedido.getFornecedor());
                            stmt.setString(2, pedido.getStatus().name());
                            stmt.setDouble(3, pedido.getValorTotal());
                            stmt.setTimestamp(4, Timestamp.valueOf(pedido.getDataPedido()));
                        },
                        (rs, entidade) -> entidade.setCodigoUnico(rs.getLong(1))
                );
            } else {
                pedido.setDataUltimaAtualizacao(LocalDateTime.now());
                if (pedido.getStatus() == null) {
                    pedido.setStatus(StatusPedidoPecas.ORCANDO);
                }
                if (pedido.getValorTotal() == null) {
                    pedido.setValorTotal(0.0);
                }

                executarUpdate(
                        connection,
                        "UPDATE pedidos SET fornecedor = ?, status = ?, valor_total = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?",
                        stmt -> {
                            stmt.setString(1, pedido.getFornecedor());
                            stmt.setString(2, pedido.getStatus().name());
                            stmt.setDouble(3, pedido.getValorTotal());
                            stmt.setTimestamp(4, Timestamp.valueOf(pedido.getDataUltimaAtualizacao()));
                            stmt.setLong(5, pedido.getCodigoUnico());
                        }
                );
            }

            executarUpdate(
                    connection,
                    "DELETE FROM itens_pedido_peca WHERE pedido_id = ?",
                    stmt -> stmt.setLong(1, pedido.getCodigoUnico())
            );

            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemPedidoPeca item : pedido.getItens()) {
                    executarUpdate(
                            connection,
                            "INSERT INTO itens_pedido_peca (pedido_id, peca_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)",
                            stmt -> {
                                stmt.setLong(1, pedido.getCodigoUnico());
                                stmt.setLong(2, item.getPeca().getCodigoUnico());
                                stmt.setInt(3, item.getQuantidade());
                                stmt.setDouble(4, item.getPrecoUnitario());
                            }
                    );
                }
            }

            return pedido;
        });
    }

    @Override
    public Optional<PedidoPecas> buscarPorId(Long id) {
        return executarBuscaPorId(
                "SELECT * FROM pedidos WHERE codigo_unico = ?",
                stmt -> stmt.setLong(1, id),
                this::mapear
        );
    }

    @Override
    public java.util.List<PedidoPecas> buscarTodos() {
        return executarBuscaTodos("SELECT * FROM pedidos", this::mapear);
    }

    @Override
    public Optional<PedidoPecas> atualizar(Long id, PedidoPecas pedido) {
        pedido.setCodigoUnico(id);
        return Optional.of(salvar(pedido));
    }

    @Override
    public boolean deletar(Long id) {
        return executarDelete(
                "DELETE FROM pedidos WHERE codigo_unico = ?",
                stmt -> stmt.setLong(1, id)
        );
    }

    private PedidoPecas mapear(ResultSet rs) throws SQLException {
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

    @Override
    protected void enriquecer(Connection connection, PedidoPecas pedido) throws SQLException {
        String sql = "SELECT * FROM itens_pedido_peca WHERE pedido_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, pedido.getCodigoUnico());

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Peca pecaMock = new Peca(null, null, null, 0, 0.0, 0.0, null);
                    pecaMock.setCodigoUnico(rs.getLong("peca_id"));

                    ItemPedidoPeca item = new ItemPedidoPeca(
                            pedido,
                            pecaMock,
                            rs.getInt("quantidade"),
                            rs.getDouble("preco_unitario")
                    );
                    pedido.getItens().add(item);
                }
            }
        }
    }
}
