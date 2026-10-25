package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.mapper.jdbc.PecaResultSetMapper;
import br.com.mecaniQA.api.model.Peca;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PecaRepository extends AbstractJdbcRepository<Peca, Long> {

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
                "INSERT INTO pecas (nome, preco, data_cadastro) VALUES (?, ?, ?)",
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
    public Optional<Peca> buscarPorId(Long id) {
        return executarBuscaPorId(
                "SELECT * FROM pecas WHERE codigo_unico = ?",
                stmt -> stmt.setLong(1, id),
                PecaResultSetMapper::map
        );
    }

    @Override
    public List<Peca> buscarTodos() {
        return executarBuscaTodos("SELECT * FROM pecas", PecaResultSetMapper::map);
    }

    @Override
    public Optional<Peca> atualizar(Long id, Peca peca) {
        peca.setDataUltimaAtualizacao(LocalDateTime.now());
        peca.setCodigoUnico(id);

        int linhas = executarUpdate(
                "UPDATE pecas SET nome = ?, preco = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?",
                stmt -> {
                    stmt.setString(1, peca.getNome());
                    stmt.setDouble(2, peca.getPrecoVenda() != null ? peca.getPrecoVenda() : 0.0);
                    stmt.setTimestamp(3, Timestamp.valueOf(peca.getDataUltimaAtualizacao()));
                    stmt.setLong(4, id);
                }
        );

        return linhas > 0 ? Optional.of(peca) : Optional.empty();
    }


    @Override
    public boolean deletar(Long id) {
        return executarDelete(
                "DELETE FROM pecas WHERE codigo_unico = ?",
                stmt -> stmt.setLong(1, id)
        );
    }
}
