package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.DatabaseConnection;
import br.com.mecaniQA.api.model.ItemPedidoPeca;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedidoPecas;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class PedidoPecasRepository {

    private static PedidoPecasRepository instance;

    private PedidoPecasRepository() {}

    public static PedidoPecasRepository getInstance() {
        if (instance == null) {
            instance = new PedidoPecasRepository();
        }
        return instance;
    }

    public PedidoPecas salvar(PedidoPecas pedido) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia a Transação (Garante que Cabeçalho e Itens salvem juntos)

            // 1. Salva ou Atualiza o Cabeçalho do Pedido
            if (pedido.getCodigoUnico() == null) {
                String sql = "INSERT INTO pedidos (fornecedor, status, valor_total, data_pedido) VALUES (?, ?, ?, ?)";
                pedido.setDataPedido(LocalDateTime.now());

                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, pedido.getFornecedor());
                    stmt.setString(2, pedido.getStatus() != null ? pedido.getStatus().name() : "ORCANDO");
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
                String sql = "UPDATE pedidos SET fornecedor = ?, status = ?, valor_total = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?";
                pedido.setDataUltimaAtualizacao(LocalDateTime.now());

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, pedido.getFornecedor());
                    stmt.setString(2, pedido.getStatus().name());
                    stmt.setDouble(3, pedido.getValorTotal());
                    stmt.setTimestamp(4, Timestamp.valueOf(pedido.getDataUltimaAtualizacao()));
                    stmt.setLong(5, pedido.getCodigoUnico());
                    stmt.executeUpdate();
                }
            }

            // 2. Limpa os itens antigos da entidade associativa para este pedido
            String sqlDelete = "DELETE FROM itens_pedido_peca WHERE pedido_id = ?";
            try (PreparedStatement stmtDel = conn.prepareStatement(sqlDelete)) {
                stmtDel.setLong(1, pedido.getCodigoUnico());
                stmtDel.executeUpdate();
            }

            // 3. Insere a nova lista de itens usando processamento em Lote (Batch)
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                String sqlInsert = "INSERT INTO itens_pedido_peca (pedido_id, peca_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmtIns = conn.prepareStatement(sqlInsert)) {
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

            conn.commit(); // Confirma a Transação no banco

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException("Erro ao salvar Pedido de Peças e seus itens.", e);
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {}
        }
        return pedido;
    }

    public Optional<PedidoPecas> buscarPorId(Long id) {
        String sqlPedido = "SELECT * FROM pedidos WHERE codigo_unico = ?";
        String sqlItens = "SELECT * FROM itens_pedido_peca WHERE pedido_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            PedidoPecas pedido = null;

            // Busca o cabeçalho
            try (PreparedStatement stmt = conn.prepareStatement(sqlPedido)) {
                stmt.setLong(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        pedido = new PedidoPecas(
                                rs.getString("fornecedor"),
                                StatusPedidoPecas.valueOf(rs.getString("status")),
                                rs.getDouble("valor_total")
                        );
                        pedido.setCodigoUnico(rs.getLong("codigo_unico"));
                        if (rs.getTimestamp("data_pedido") != null) {
                            pedido.setDataPedido(rs.getTimestamp("data_pedido").toLocalDateTime());
                        }
                    }
                }
            }

            // Busca os itens vinculados ao pedido
            if (pedido != null) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlItens)) {
                    stmt.setLong(1, id);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            // Correção aplicada aqui: Usando o construtor com 7 argumentos passando null/0
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
                return Optional.of(pedido);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar Pedido de Peças.", e);
        }
        return Optional.empty();
    }

    public boolean deletar(Long id) {
        String sql = "DELETE FROM pedidos WHERE codigo_unico = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Pedido.", e);
        }
    }
}