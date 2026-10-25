package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.StatusOrdemServico;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrdemServicoRepository extends AbstractJdbcRepository<OrdemServico, Long> {

    private static OrdemServicoRepository instance;

    private OrdemServicoRepository() {}

    public static OrdemServicoRepository getInstance() {
        if (instance == null) {
            instance = new OrdemServicoRepository();
        }
        return instance;
    }

    @Override
    public OrdemServico salvar(OrdemServico os) {
        if (os.getCodigoUnico() != null) {
            atualizar(os.getCodigoUnico(), os);
            return os;
        }

        os.setDataAbertura(LocalDateTime.now());
        if (os.getStatus() == null) {
            os.setStatus(StatusOrdemServico.ABERTO);
        }
        if (os.getValorTotal() == null) {
            os.setValorTotal(0.0);
        }

        return executarSalvar(
                "INSERT INTO ordens_servico (descricao_problema, status, valor_total, data_abertura) VALUES (?, ?, ?, ?)",
                os,
                stmt -> {
                    stmt.setString(1, os.getDescricaoProblema());
                    stmt.setString(2, os.getStatus().name());
                    stmt.setDouble(3, os.getValorTotal());
                    stmt.setTimestamp(4, Timestamp.valueOf(os.getDataAbertura()));
                },
                (rs, entidade) -> entidade.setCodigoUnico(rs.getLong(1))
        );
    }

    @Override
    public Optional<OrdemServico> buscarPorId(Long id) {
        return executarBuscaPorId(
                "SELECT * FROM ordens_servico WHERE codigo_unico = ?",
                stmt -> stmt.setLong(1, id),
                this::mapear
        );
    }

    @Override
    public List<OrdemServico> buscarTodos() {
        return executarBuscaTodos("SELECT * FROM ordens_servico", this::mapear);
    }

    @Override
    public Optional<OrdemServico> atualizar(Long id, OrdemServico os) {
        os.setCodigoUnico(id);
        os.setDataUltimaAtualizacao(LocalDateTime.now());
        if (os.getStatus() == null) {
            os.setStatus(StatusOrdemServico.ABERTO);
        }
        if (os.getValorTotal() == null) {
            os.setValorTotal(0.0);
        }

        int linhas = executarUpdate(
                "UPDATE ordens_servico SET descricao_problema = ?, status = ?, valor_total = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?",
                stmt -> {
                    stmt.setString(1, os.getDescricaoProblema());
                    stmt.setString(2, os.getStatus().name());
                    stmt.setDouble(3, os.getValorTotal());
                    stmt.setTimestamp(4, Timestamp.valueOf(os.getDataUltimaAtualizacao()));
                    stmt.setLong(5, id);
                }
        );

        return linhas > 0 ? Optional.of(os) : Optional.empty();
    }

    @Override
    public boolean deletar(Long id) {
        return executarDelete(
                "DELETE FROM ordens_servico WHERE codigo_unico = ?",
                stmt -> stmt.setLong(1, id)
        );
    }

    private OrdemServico mapear(ResultSet rs) throws SQLException {
        return OrdemServico.builder()
                .codigoUnico(rs.getLong("codigo_unico"))
                .descricaoProblema(rs.getString("descricao_problema"))
                .status(StatusOrdemServico.valueOf(rs.getString("status")))
                .valorTotal(rs.getDouble("valor_total"))
                .dataAbertura(rs.getTimestamp("data_abertura") != null ? rs.getTimestamp("data_abertura").toLocalDateTime() : null)
                .dataUltimaAtualizacao(rs.getTimestamp("data_ultima_atualizacao") != null ? rs.getTimestamp("data_ultima_atualizacao").toLocalDateTime() : null)
                .servicos(new ArrayList<>())
                .pedidosPecas(new ArrayList<>())
                .build();
    }
}
