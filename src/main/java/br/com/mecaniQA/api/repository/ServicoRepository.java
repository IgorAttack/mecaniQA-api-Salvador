package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Servico;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicoRepository {

    private static ServicoRepository instance;
    private List<Servico> tabelaServicos;
    private Long contadorId;

    private ServicoRepository() {
        this.tabelaServicos = new ArrayList<>();
        this.contadorId = 1L;
    }

    public static ServicoRepository getInstance() {
        if (instance == null) {
            instance = new ServicoRepository();
        }
        return instance;
    }

    public Servico salvar(Servico servico) {
        servico.setCodigoUnico(contadorId++);
        servico.setDataCriacao(LocalDateTime.now());
        this.tabelaServicos.add(servico);
        return servico;
    }

    public List<Servico> buscarTodos() {
        return this.tabelaServicos;
    }

    public Optional<Servico> buscarPorId(Long id) {
        return tabelaServicos.stream()
                .filter(s -> s.getCodigoUnico().equals(id))
                .findFirst();
    }
    public Optional<Servico> atualizar(Long id, Servico servico) {
        Optional<Servico> existente = buscarPorId(id);
        if (existente.isPresent()) {
            int index = tabelaServicos.indexOf(existente.get());
            servico.setCodigoUnico(id);
            servico.setDataUltimaAtualizacao(LocalDateTime.now());
            tabelaServicos.set(index, servico);
            return Optional.of(servico);
        }
        return Optional.empty();
    }

    public boolean deletar(Long id) {
        return tabelaServicos.removeIf(s -> s.getCodigoUnico().equals(id));
    }
}