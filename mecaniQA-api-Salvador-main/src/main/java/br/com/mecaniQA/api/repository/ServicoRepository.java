package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.DatabaseConnection;
import br.com.mecaniQA.api.model.Servico;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicoRepository {

    private static ServicoRepository instance;

    private ServicoRepository() {}

    public static ServicoRepository getInstance() {
        if (instance == null) {
            instance = new ServicoRepository();
        }
        return instance;
    }

    public Servico salvar(Servico servico) {
        String sql = "INSERT INTO servicos (nome, preco, data_criacao) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            servico.setDataCriacao(LocalDateTime.now());

            stmt.setString(1, servico.getNomeServico());
            stmt.setDouble(2, servico.getCustoTabelado() != null ? servico.getCustoTabelado() : 0.0);
            stmt.setTimestamp(3, Timestamp.valueOf(servico.getDataCriacao()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    servico.setCodigoUnico(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar Serviço.", e);
        }
        return servico;
    }

    public List<Servico> buscarTodos() {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servicos";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                servicos.add(mapearServico(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Serviços.", e);
        }
        return servicos;
    }

    public Optional<Servico> buscarPorId(Long id) {
        String sql = "SELECT * FROM servicos WHERE codigo_unico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearServico(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar Serviço.", e);
        }
        return Optional.empty();
    }

    public Optional<Servico> atualizar(Long id, Servico servico) {
        String sql = "UPDATE servicos SET nome = ?, preco = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            servico.setDataUltimaAtualizacao(LocalDateTime.now());

            stmt.setString(1, servico.getNomeServico());
            stmt.setDouble(2, servico.getCustoTabelado() != null ? servico.getCustoTabelado() : 0.0);
            stmt.setTimestamp(3, Timestamp.valueOf(servico.getDataUltimaAtualizacao()));
            stmt.setLong(4, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                servico.setCodigoUnico(id);
                return Optional.of(servico);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Serviço.", e);
        }
        return Optional.empty();
    }

    public boolean deletar(Long id) {
        String sql = "DELETE FROM servicos WHERE codigo_unico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Serviço.", e);
        }
    }

    private Servico mapearServico(ResultSet rs) throws SQLException {
        // Usa o construtor da classe Model, passando 0 para o tempo estimado como padrão caso não tenha no DB
        Servico servico = new Servico(rs.getString("nome"), 0, rs.getDouble("preco"));
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