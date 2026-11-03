package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJdbcRepository<T, ID> implements IRepository<T, ID> {

    @FunctionalInterface
    protected interface StatementSetter {
        void setParameters(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    protected interface KeyGeneratedHandler<T> {
        void handleKey(ResultSet rs, T entity) throws SQLException;
    }

    @FunctionalInterface
    protected interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }

    protected T executarSalvar(String sql, T entidade, StatementSetter setter, KeyGeneratedHandler<T> keyHandler) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (setter != null) {
                setter.setParameters(stmt);
            }
            stmt.executeUpdate();

            if (keyHandler != null) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        keyHandler.handleKey(rs, entidade);
                    }
                }
            }
            return entidade;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar operação de salvamento no banco de dados: " + e.getMessage(), e);
        }
    }

    protected Optional<T> executarBuscaPorId(String sql, StatementSetter setter, RowMapper<T> mapper) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (setter != null) {
                setter.setParameters(stmt);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(mapper.mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar busca por ID no banco de dados: " + e.getMessage(), e);
        }
    }

    protected List<T> executarBuscaTodos(String sql, RowMapper<T> mapper) {
        List<T> resultados = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                resultados.add(mapper.mapRow(rs));
            }
            return resultados;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar registros do banco de dados: " + e.getMessage(), e);
        }
    }

    protected int executarUpdate(String sql, StatementSetter setter) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (setter != null) {
                setter.setParameters(stmt);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar atualização no banco de dados: " + e.getMessage(), e);
        }
    }

    protected boolean executarDelete(String sql, StatementSetter setter) {
        return executarUpdate(sql, setter) > 0;
    }
}