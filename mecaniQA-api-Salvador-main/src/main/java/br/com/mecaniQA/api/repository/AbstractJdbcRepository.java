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

/**
 * Template Method para execução de operações JDBC comuns aos repositórios.
 *
 * <p>A classe concentra o boilerplate de conexão, PreparedStatement,
 * ResultSet, chaves geradas, atualização, exclusão e tratamento de
 * SQLException. Os repositórios informam apenas a SQL e os parâmetros por
 * meio de interfaces funcionais (lambdas).</p>
 */
public abstract class AbstractJdbcRepository<T, ID> implements IRepository<T, ID> {

    @FunctionalInterface
    protected interface StatementPreparer {
        void prepare(PreparedStatement statement) throws SQLException;
    }

    @FunctionalInterface
    protected interface ResultSetMapper<R> {
        R map(ResultSet resultSet) throws SQLException;
    }

    @FunctionalInterface
    protected interface GeneratedKeyHandler<R> {
        void apply(ResultSet resultSet, R entity) throws SQLException;
    }

    /**
     * Representa o trabalho executado dentro de uma transação JDBC.
     * A Connection é controlada exclusivamente pela classe base.
     */
    @FunctionalInterface
    protected interface TransactionalWork<R> {
        R execute(Connection connection) throws SQLException;
    }

    /**
     * Executa uma operação completa dentro de uma transação.
     *
     * <p>A classe base assume a responsabilidade por abrir a conexão,
     * desabilitar o auto-commit, realizar commit, rollback em caso de erro
     * e fechar a conexão. Os repositórios ficam responsáveis somente pelas
     * operações SQL que fazem parte da transação.</p>
     */
    protected <R> R executarEmTransacao(TransactionalWork<R> trabalho) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            boolean autoCommitOriginal = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                R resultado = trabalho.execute(connection);
                connection.commit();
                return resultado;
            } catch (SQLException | RuntimeException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            } finally {
                try {
                    connection.setAutoCommit(autoCommitOriginal);
                } catch (SQLException resetException) {
                    // A conexão será fechada pelo try-with-resources.
                }
            }
        } catch (SQLException exception) {
            throw erro("Erro ao gerenciar transação JDBC.", exception);
        }
    }

    protected Optional<T> executarBuscaPorId(
            String sql,
            StatementPreparer preparador,
            ResultSetMapper<T> mapper) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            preparador.prepare(statement);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                T entity = mapper.map(resultSet);
                if (entity != null) {
                    enriquecer(connection, entity);
                }
                return Optional.ofNullable(entity);
            }
        } catch (SQLException exception) {
            throw erro("Erro ao buscar registro por ID.", exception);
        }
    }

    protected List<T> executarBuscaTodos(
            String sql,
            ResultSetMapper<T> mapper) {
        List<T> resultado = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                T entity = mapper.map(resultSet);
                if (entity != null) {
                    enriquecer(connection, entity);
                }
                resultado.add(entity);
            }

            return resultado;
        } catch (SQLException exception) {
            throw erro("Erro ao listar registros.", exception);
        }
    }

    protected T executarSalvar(
            String sql,
            T entity,
            StatementPreparer preparador,
            GeneratedKeyHandler<T> chaveGerada) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return executarSalvar(connection, sql, entity, preparador, chaveGerada);
        } catch (SQLException exception) {
            throw erro("Erro ao salvar registro.", exception);
        }
    }

    protected T executarSalvar(
            Connection connection,
            String sql,
            T entity,
            StatementPreparer preparador,
            GeneratedKeyHandler<T> chaveGerada) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            preparador.prepare(statement);
            statement.executeUpdate();

            if (chaveGerada != null) {
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        chaveGerada.apply(resultSet, entity);
                    }
                }
            }

            return entity;
        }
    }

    protected int executarUpdate(
            String sql,
            StatementPreparer preparador) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return executarUpdate(connection, sql, preparador);
        } catch (SQLException exception) {
            throw erro("Erro ao executar atualização.", exception);
        }
    }

    protected int executarUpdate(
            Connection connection,
            String sql,
            StatementPreparer preparador) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            preparador.prepare(statement);
            return statement.executeUpdate();
        }
    }

    protected boolean executarDelete(
            String sql,
            StatementPreparer preparador) {
        return executarUpdate(sql, preparador) > 0;
    }

    /** Hook opcional para entidades que precisam carregar dados complementares. */
    protected void enriquecer(Connection connection, T entity) throws SQLException {
        // Implementação padrão: nenhuma ação adicional.
    }

    protected RuntimeException erro(String mensagem, SQLException exception) {
        return new RuntimeException(mensagem, exception);
    }
}
