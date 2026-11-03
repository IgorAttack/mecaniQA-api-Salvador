package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.mapper.JDBC.PecaResultSetMapper;
import br.com.mecaniQA.api.model.Peca;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PecaRepository extends AbstractJdbcRepository<Peca, Long> {

    private static final String SQL_INSERT = "INSERT INTO pecas (nome, preco, data_cadastro) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT * FROM pecas";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM pecas WHERE codigo_unico = ?";
    private static final String SQL_UPDATE = "UPDATE pecas SET nome = ?, preco = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?";
    private static final String SQL_DELETE = "DELETE FROM pecas WHERE codigo_unico = ?";

    private static PecaRepository instance;

    private PecaRepository() {}

    public static PecaRepository getInstance() {
        if (instance == null) {
            instance = new PecaRepository();
        }
        return instance;
    }

    @Override
    public Peca salvar(Peca peca) {
        peca.setDataCadastro(LocalDateTime.now());
        return executarSalvar(
                SQL_INSERT,
                peca,
                stmt -> {
                    stmt.setString(1, peca.getNome());
                    stmt.setDouble(2, peca.getPrecoVenda() != null ? peca.getPrecoVenda() : 0.0);
                    stmt.setTimestamp(3, Timestamp.valueOf(peca.getDataCadastro()));
                },
                (rs, entidade) -> entidade.setCodigoUnico(rs.getLong(1))
        );
    }

    @Override
    public List<Peca> buscarTodos() {
        return executarBuscaTodos(SQL_SELECT_ALL, PecaResultSetMapper::map);
    }

    public List<Peca> buscarTodas() {
        return buscarTodos();
    }

    @Override
    public Optional<Peca> buscarPorId(Long id) {
        return executarBuscaPorId(
                SQL_SELECT_BY_ID,
                stmt -> stmt.setLong(1, id),
                PecaResultSetMapper::map
        );
    }

    @Override
    public Optional<Peca> atualizar(Long id, Peca peca) {
        peca.setDataUltimaAtualizacao(LocalDateTime.now());
        peca.setCodigoUnico(id);

        int affectedRows = executarUpdate(
                SQL_UPDATE,
                stmt -> {
                    stmt.setString(1, peca.getNome());
                    stmt.setDouble(2, peca.getPrecoVenda() != null ? peca.getPrecoVenda() : 0.0);
                    stmt.setTimestamp(3, Timestamp.valueOf(peca.getDataUltimaAtualizacao()));
                    stmt.setLong(4, id);
                }
        );

        return affectedRows > 0 ? Optional.of(peca) : Optional.empty();
    }

    @Override
    public boolean deletar(Long id) {
        return executarDelete(
                SQL_DELETE,
                stmt -> stmt.setLong(1, id)
        );
    }
}