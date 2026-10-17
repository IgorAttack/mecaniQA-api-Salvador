package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.DatabaseConnection;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.StatusOrdemServico;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class OrdemServicoRepository {

    private static OrdemServicoRepository instance;

    private OrdemServicoRepository() {}

    public static OrdemServicoRepository getInstance() {
        if (instance == null) {
            instance = new OrdemServicoRepository();
        }
        return instance;
    }

    public OrdemServico salvar(OrdemServico os) {
        if (os.getCodigoUnico() == null) {
            String sql = "INSERT INTO ordens_servico (descricao_problema, status, valor_total, data_abertura) VALUES (?, ?, ?, ?)";
            os.setDataAbertura(LocalDateTime.now());

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, os.getDescricaoProblema());
                stmt.setString(2, os.getStatus() != null ? os.getStatus().name() : StatusOrdemServico.ABERTO.name());
                stmt.setDouble(3, os.getValorTotal() != null ? os.getValorTotal() : 0.0);
                stmt.setTimestamp(4, Timestamp.valueOf(os.getDataAbertura()));

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        os.setCodigoUnico(rs.getLong(1));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao criar Ordem de Servico.", e);
            }
        } else {
            String sql = "UPDATE ordens_servico SET descricao_problema = ?, status = ?, valor_total = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?";
            os.setDataUltimaAtualizacao(LocalDateTime.now());

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, os.getDescricaoProblema());
                stmt.setString(2, os.getStatus().name());
                stmt.setDouble(3, os.getValorTotal());
                stmt.setTimestamp(4, Timestamp.valueOf(os.getDataUltimaAtualizacao()));
                stmt.setLong(5, os.getCodigoUnico());

                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao atualizar Ordem de Servico.", e);
            }
        }
        return os;
    }

    public Optional<OrdemServico> buscarPorId(Long id) {
        String sql = "SELECT * FROM ordens_servico WHERE codigo_unico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Utiliza o Padrão Builder da Model
                    OrdemServico os = OrdemServico.builder()
                            .codigoUnico(rs.getLong("codigo_unico"))
                            .descricaoProblema(rs.getString("descricao_problema"))
                            .status(StatusOrdemServico.valueOf(rs.getString("status")))
                            .valorTotal(rs.getDouble("valor_total"))
                            .dataAbertura(rs.getTimestamp("data_abertura") != null ? rs.getTimestamp("data_abertura").toLocalDateTime() : null)
                            .dataUltimaAtualizacao(rs.getTimestamp("data_ultima_atualizacao") != null ? rs.getTimestamp("data_ultima_atualizacao").toLocalDateTime() : null)
                            .servicos(new ArrayList<>())
                            .pedidosPecas(new ArrayList<>())
                            .build();

                    return Optional.of(os);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar OS por ID.", e);
        }
        return Optional.empty();
    }

    public boolean deletar(Long id) {
        String sql = "DELETE FROM ordens_servico WHERE codigo_unico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar OS.", e);
        }
    }
}