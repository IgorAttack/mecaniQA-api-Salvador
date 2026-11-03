package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.DatabaseConnection;
import br.com.mecaniQA.api.mapper.JDBC.ItemPedidoPecaResultSetMapper;
import br.com.mecaniQA.api.mapper.JDBC.PedidoPecasResultSetMapper;
import br.com.mecaniQA.api.model.ItemPedidoPeca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedidoPecas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PedidoPecasRepository extends AbstractJdbcRepository<PedidoPecas, Long> {

    private static final String SQL_INSERT_PEDIDO = "INSERT INTO pedidos (fornecedor, status, valor_total, data_pedido) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_PEDIDO = "UPDATE pedidos SET fornecedor = ?, status = ?, valor_total = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM pedidos WHERE codigo_unico = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM pedidos";
    private static final String SQL_DELETE_PEDIDO = "DELETE FROM pedidos WHERE codigo_unico = ?";
    private static final String SQL_DELETE_ITENS = "DELETE FROM itens_pedido_peca WHERE pedido_id = ?";
    private static final String SQL_INSERT_ITEM = "INSERT INTO itens_pedido_peca (pedido_id, peca_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
    private static final String SQL_SELECT_ITENS_BY_PEDIDO = "SELECT * FROM itens_pedido_peca WHERE pedido_id = ?";

    private static PedidoPecasRepository instance;

    private PedidoPecasRepository() {}

    public static PedidoPecasRepository getInstance() {
        if (instance == null) {
            instance = new PedidoPecasRepository();
        }
        return instance;
    }

    @Override
    public PedidoPecas salvar(PedidoPecas pedido) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (pedido.getCodigoUnico() == null) {
                pedido.setDataPedido(LocalDateTime.now());

                try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_PEDIDO, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, pedido.getFornecedor());
                    stmt.setString(2, pedido.getStatus() != null ? pedido.getStatus().name() : StatusPedidoPecas.ORCANDO.name());
                    stmt.setDouble(3, pedido.getValorTotal() != null ? pedido.getValorTotal() : 0.0);
                    stmt.setTimestamp(4, Timestamp.valueOf(pedido.getDataPedido()));
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            pedido.setCodigoUnico(rs.getLong(1));
                        }
                    }
                }
            } else {
                pedido.setDataUltimaAtualizacao(LocalDateTime.now());

                try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PEDIDO)) {
                    stmt.setString(1, pedido.getFornecedor());
                    stmt.setString(2, pedido.getStatus() != null ? pedido.getStatus().name() : StatusPedidoPecas.ORCANDO.name());
                    stmt.setDouble(3, pedido.getValorTotal() != null ? pedido.getValorTotal() : 0.0);
                    stmt.setTimestamp(4, Timestamp.valueOf(pedido.getDataUltimaAtualizacao()));
                    stmt.setLong(5, pedido.getCodigoUnico());
                    stmt.executeUpdate();
                }
            }

            try (PreparedStatement stmtDel = conn.prepareStatement(SQL_DELETE_ITENS)) {
                stmtDel.setLong(1, pedido.getCodigoUnico());
                stmtDel.executeUpdate();
            }

            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                try (PreparedStatement stmtIns = conn.prepareStatement(SQL_INSERT_ITEM)) {
                    for (ItemPedidoPeca item : pedido.getItens()) {
                        stmtIns.setLong(1, pedido.getCodigoUnico());
                        stmtIns.setLong(2, item.getPeca().getCodigoUnico());
                        stmtIns.setInt(3, item.getQuantidade());
                        stmtIns.setDouble(4, item.getPrecoUnitario());
                        stmtIns.addBatch();
                    }
                    stmtIns.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Erro ao salvar Pedido de Peças e seus itens.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
        return pedido;
    }

    @Override
    public Optional<PedidoPecas> buscarPorId(Long id) {
        Optional<PedidoPecas> pedidoOpt = executarBuscaPorId(
                SQL_SELECT_BY_ID,
                stmt -> stmt.setLong(1, id),
                PedidoPecasResultSetMapper::map
        );

        pedidoOpt.ifPresent(this::carregarItensDoPedido);
        return pedidoOpt;
    }

    @Override
    public List<PedidoPecas> buscarTodos() {
        List<PedidoPecas> pedidos = executarBuscaTodos(SQL_SELECT_ALL, PedidoPecasResultSetMapper::map);
        pedidos.forEach(this::carregarItensDoPedido);
        return pedidos;
    }

    @Override
    public Optional<PedidoPecas> atualizar(Long id, PedidoPecas pedido) {
        if (buscarPorId(id).isEmpty()) {
            return Optional.empty();
        }
        pedido.setCodigoUnico(id);
        return Optional.of(salvar(pedido));
    }

    @Override
    public boolean deletar(Long id) {
        return executarDelete(
                SQL_DELETE_PEDIDO,
                stmt -> stmt.setLong(1, id)
        );
    }

    private void carregarItensDoPedido(PedidoPecas pedido) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ITENS_BY_PEDIDO)) {

            stmt.setLong(1, pedido.getCodigoUnico());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedido.getItens().add(ItemPedidoPecaResultSetMapper.map(rs, pedido));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar itens do Pedido de Peças.", e);
        }
    }
}