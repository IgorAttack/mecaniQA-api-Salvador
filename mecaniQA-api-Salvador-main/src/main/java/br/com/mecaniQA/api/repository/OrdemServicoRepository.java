package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.DatabaseConnection;
import br.com.mecaniQA.api.mapper.JDBC.OrdemServicoResultSetMapper;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.model.StatusOrdemServico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrdemServicoRepository extends AbstractJdbcRepository<OrdemServico, Long> {

    private static final String SQL_INSERT = "INSERT INTO ordens_servico (descricao_problema, status, valor_total, data_abertura) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE ordens_servico SET descricao_problema = ?, status = ?, valor_total = ?, data_ultima_atualizacao = ? WHERE codigo_unico = ?";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM ordens_servico WHERE codigo_unico = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM ordens_servico";
    private static final String SQL_DELETE = "DELETE FROM ordens_servico WHERE codigo_unico = ?";

    private static final String SQL_DELETE_OS_SERVICOS = "DELETE FROM os_servicos WHERE os_id = ?";
    private static final String SQL_INSERT_OS_SERVICO = "INSERT INTO os_servicos (os_id, servico_id) VALUES (?, ?)";
    private static final String SQL_SELECT_OS_SERVICOS = "SELECT servico_id FROM os_servicos WHERE os_id = ?";

    private static final String SQL_DELETE_OS_PEDIDOS = "DELETE FROM os_pedidos WHERE os_id = ?";
    private static final String SQL_INSERT_OS_PEDIDO = "INSERT INTO os_pedidos (os_id, pedido_id) VALUES (?, ?)";
    private static final String SQL_SELECT_OS_PEDIDOS = "SELECT pedido_id FROM os_pedidos WHERE os_id = ?";

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
        if (os.getCodigoUnico() == null) {
            os.setDataAbertura(LocalDateTime.now());
            executarSalvar(
                    SQL_INSERT,
                    os,
                    stmt -> {
                        stmt.setString(1, os.getDescricaoProblema());
                        stmt.setString(2, os.getStatus() != null ? os.getStatus().name() : StatusOrdemServico.ABERTO.name());
                        stmt.setDouble(3, os.getValorTotal() != null ? os.getValorTotal() : 0.0);
                        stmt.setTimestamp(4, Timestamp.valueOf(os.getDataAbertura()));
                    },
                    (rs, entidade) -> entidade.setCodigoUnico(rs.getLong(1))
            );
        } else {
            os.setDataUltimaAtualizacao(LocalDateTime.now());
            executarUpdate(
                    SQL_UPDATE,
                    stmt -> {
                        stmt.setString(1, os.getDescricaoProblema());
                        stmt.setString(2, os.getStatus() != null ? os.getStatus().name() : StatusOrdemServico.ABERTO.name());
                        stmt.setDouble(3, os.getValorTotal() != null ? os.getValorTotal() : 0.0);
                        stmt.setTimestamp(4, Timestamp.valueOf(os.getDataUltimaAtualizacao()));
                        stmt.setLong(5, os.getCodigoUnico());
                    }
            );
        }

        salvarRelacionamentos(os);
        return os;
    }

    @Override
    public Optional<OrdemServico> buscarPorId(Long id) {
        Optional<OrdemServico> osOpt = executarBuscaPorId(
                SQL_SELECT_BY_ID,
                stmt -> stmt.setLong(1, id),
                OrdemServicoResultSetMapper::map
        );

        osOpt.ifPresent(this::carregarRelacionamentos);
        return osOpt;
    }

    @Override
    public List<OrdemServico> buscarTodos() {
        List<OrdemServico> ordens = executarBuscaTodos(SQL_SELECT_ALL, OrdemServicoResultSetMapper::map);
        ordens.forEach(this::carregarRelacionamentos);
        return ordens;
    }

    @Override
    public Optional<OrdemServico> atualizar(Long id, OrdemServico os) {
        if (buscarPorId(id).isEmpty()) {
            return Optional.empty();
        }
        os.setCodigoUnico(id);
        return Optional.of(salvar(os));
    }

    @Override
    public boolean deletar(Long id) {
        return executarDelete(
                SQL_DELETE,
                stmt -> stmt.setLong(1, id)
        );
    }

    private void salvarRelacionamentos(OrdemServico os) {
        if (os.getCodigoUnico() == null) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement delSrv = conn.prepareStatement(SQL_DELETE_OS_SERVICOS)) {
                delSrv.setLong(1, os.getCodigoUnico());
                delSrv.executeUpdate();
            }
            if (os.getServicos() != null && !os.getServicos().isEmpty()) {
                try (PreparedStatement insSrv = conn.prepareStatement(SQL_INSERT_OS_SERVICO)) {
                    for (Servico s : os.getServicos()) {
                        if (s != null && s.getCodigoUnico() != null) {
                            insSrv.setLong(1, os.getCodigoUnico());
                            insSrv.setLong(2, s.getCodigoUnico());
                            insSrv.addBatch();
                        }
                    }
                    insSrv.executeBatch();
                }
            }

            try (PreparedStatement delPed = conn.prepareStatement(SQL_DELETE_OS_PEDIDOS)) {
                delPed.setLong(1, os.getCodigoUnico());
                delPed.executeUpdate();
            }
            if (os.getPedidosPecas() != null && !os.getPedidosPecas().isEmpty()) {
                try (PreparedStatement insPed = conn.prepareStatement(SQL_INSERT_OS_PEDIDO)) {
                    for (PedidoPecas p : os.getPedidosPecas()) {
                        if (p != null && p.getCodigoUnico() != null) {
                            insPed.setLong(1, os.getCodigoUnico());
                            insPed.setLong(2, p.getCodigoUnico());
                            insPed.addBatch();
                        }
                    }
                    insPed.executeBatch();
                }
            }
        } catch (SQLException ignored) {
            // Fallback seguro caso as tabelas associativas os_servicos/os_pedidos ainda não tenham sido rodadas no MySQL local
        }
    }

    private void carregarRelacionamentos(OrdemServico os) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmtSrv = conn.prepareStatement(SQL_SELECT_OS_SERVICOS)) {
                stmtSrv.setLong(1, os.getCodigoUnico());
                try (ResultSet rs = stmtSrv.executeQuery()) {
                    while (rs.next()) {
                        Long servicoId = rs.getLong("servico_id");
                        ServicoRepository.getInstance().buscarPorId(servicoId)
                                .ifPresent(os.getServicos()::add);
                    }
                }
            }

            try (PreparedStatement stmtPed = conn.prepareStatement(SQL_SELECT_OS_PEDIDOS)) {
                stmtPed.setLong(1, os.getCodigoUnico());
                try (ResultSet rs = stmtPed.executeQuery()) {
                    while (rs.next()) {
                        Long pedidoId = rs.getLong("pedido_id");
                        PedidoPecasRepository.getInstance().buscarPorId(pedidoId)
                                .ifPresent(os.getPedidosPecas()::add);
                    }
                }
            }
        } catch (SQLException ignored) {
        }
    }
}