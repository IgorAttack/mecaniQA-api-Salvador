package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.DatabaseConnection;
import br.com.mecaniQA.api.model.Peca;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PecaRepository {

    private static PecaRepository instance;

    private PecaRepository() {}

    public static PecaRepository getInstance() {
        if (instance == null) {
            instance = new PecaRepository();
        }
        return instance;
    }

    public Peca salvar(Peca peca) {
        String sql = "INSERT INTO pecas (nome, preco, data_cadastro) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            peca.setDataCadastro(LocalDateTime.now());

            stmt.setString(1, peca.getNome());
            stmt.setDouble(2, peca.getPrecoVenda() != null ? peca.getPrecoVenda() : 0.0);
            stmt.setTimestamp(3, Timestamp.valueOf(peca.getDataCadastro()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    peca.setCodigoUnico(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar a Peça no banco de dados.", e);
        }
        return peca;
    }

    public List<Peca> buscarTodas() {
        List<Peca> pecas = new ArrayList<>();
        String sql = "SELECT * FROM pecas";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pecas.add(mapearPeca(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Peças.", e);
        }
        return pecas;
    }

    public Optional<Peca> buscarPorId(Long id) {
        String sql = "SELECT * FROM pecas WHERE codigo_unico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPeca(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar Peça por ID.", e);
        }
        return Optional.empty();
    }

    public Optional<Peca> atualizar(Long id, Peca peca) {
        String sql = "UPDATE pecas SET nome = ?, preco = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            peca.setDataUltimaAtualizacao(LocalDateTime.now());

            stmt.setString(1, peca.getNome());
            stmt.setDouble(2, peca.getPrecoVenda() != null ? peca.getPrecoVenda() : 0.0);
            stmt.setTimestamp(3, Timestamp.valueOf(peca.getDataUltimaAtualizacao()));
            stmt.setLong(4, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                peca.setCodigoUnico(id);
                return Optional.of(peca);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar a Peça.", e);
        }
        return Optional.empty();
    }

    public boolean deletar(Long id) {
        String sql = "DELETE FROM pecas WHERE codigo_unico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar a Peça.", e);
        }
    }

    private Peca mapearPeca(ResultSet rs) throws SQLException {
        // Usa o construtor da classe Model, passando nulo para campos ainda não persistidos no BD inicial
        Peca peca = new Peca(
                rs.getString("nome"),
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
        if (rs.getTimestamp("data_ultima_atualizacao") != null) {
            peca.setDataUltimaAtualizacao(rs.getTimestamp("data_ultima_atualizacao").toLocalDateTime());
        }
        return peca;
    }
}