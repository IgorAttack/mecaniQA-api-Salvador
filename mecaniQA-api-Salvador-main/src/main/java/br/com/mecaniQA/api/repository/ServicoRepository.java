package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Servico;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ServicoRepository extends AbstractJdbcRepository<Servico, Long> {

    private static ServicoRepository instance;

    private ServicoRepository() {}

    public static ServicoRepository getInstance() {
        if (instance == null) {
            instance = new ServicoRepository();
        }
        return instance;
    }

    @Override
    public Servico salvar(Servico servico) {
        servico.setDataCriacao(LocalDateTime.now());
        return executarSalvar(
                "INSERT INTO servicos (nome, preco, data_criacao) VALUES (?, ?, ?)",
                servico,
                stmt -> {
                    stmt.setString(1, servico.getNomeServico());
                    stmt.setDouble(2, servico.getCustoTabelado() != null ? servico.getCustoTabelado() : 0.0);
                    stmt.setTimestamp(3, Timestamp.valueOf(servico.getDataCriacao()));
                },
                (rs, entidade) -> entidade.setCodigoUnico(rs.getLong(1))
        );
    }

    @Override
    public Optional<Servico> buscarPorId(Long id) {
        return executarBuscaPorId(
                "SELECT * FROM servicos WHERE codigo_unico = ?",
                stmt -> stmt.setLong(1, id),
                this::mapear
        );
    }

    @Override
    public List<Servico> buscarTodos() {
        return executarBuscaTodos("SELECT * FROM servicos", this::mapear);
    }

    @Override
    public Optional<Servico> atualizar(Long id, Servico servico) {
        servico.setDataUltimaAtualizacao(LocalDateTime.now());
        servico.setCodigoUnico(id);

        int linhas = executarUpdate(
                "UPDATE servicos SET nome = ?, preco = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?",
                stmt -> {
                    stmt.setString(1, servico.getNomeServico());
                    stmt.setDouble(2, servico.getCustoTabelado() != null ? servico.getCustoTabelado() : 0.0);
                    stmt.setTimestamp(3, Timestamp.valueOf(servico.getDataUltimaAtualizacao()));
                    stmt.setLong(4, id);
                }
        );

        return linhas > 0 ? Optional.of(servico) : Optional.empty();
    }

    @Override
    public boolean deletar(Long id) {
        return executarDelete(
                "DELETE FROM servicos WHERE codigo_unico = ?",
                stmt -> stmt.setLong(1, id)
        );
    }

    private Servico mapear(ResultSet rs) throws SQLException {
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
