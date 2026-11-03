package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.mapper.JDBC.ServicoResultSetMapper;
import br.com.mecaniQA.api.model.Servico;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ServicoRepository extends AbstractJdbcRepository<Servico, Long> {

    private static final String SQL_INSERT = "INSERT INTO servicos (nome, preco, data_criacao) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM servicos WHERE codigo_unico = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM servicos";
    private static final String SQL_UPDATE = "UPDATE servicos SET nome = ?, preco = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?";
    private static final String SQL_DELETE = "DELETE FROM servicos WHERE codigo_unico = ?";

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
                SQL_INSERT,
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
                SQL_SELECT_BY_ID,
                stmt -> stmt.setLong(1, id),
                ServicoResultSetMapper::map
        );
    }

    @Override
    public List<Servico> buscarTodos() {
        return executarBuscaTodos(SQL_SELECT_ALL, ServicoResultSetMapper::map);
    }

    @Override
    public Optional<Servico> atualizar(Long id, Servico servico) {
        servico.setDataUltimaAtualizacao(LocalDateTime.now());
        servico.setCodigoUnico(id);

        int linhas = executarUpdate(
                SQL_UPDATE,
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
                SQL_DELETE,
                stmt -> stmt.setLong(1, id)
        );
    }
}