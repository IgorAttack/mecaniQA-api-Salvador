package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Peca;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PecaRepository {

    private static PecaRepository instance;
    private List<Peca> tabelaPecas;
    private Long contadorId;

    private PecaRepository() {
        this.tabelaPecas = new ArrayList<>();
        this.contadorId = 1L;
    }

    public static PecaRepository getInstance() {
        if (instance == null) {
            instance = new PecaRepository();
        }
        return instance;
    }

    public Peca salvar(Peca peca) {
        peca.setCodigoUnico(contadorId++);
        peca.setDataCadastro(LocalDateTime.now());
        this.tabelaPecas.add(peca);
        return peca;
    }

    public List<Peca> buscarTodas() {
        return this.tabelaPecas;
    }

    public Optional<Peca> buscarPorId(Long id) {
        return tabelaPecas.stream()
                .filter(p -> p.getCodigoUnico().equals(id))
                .findFirst();
    }
    public Optional<Peca> atualizar(Long id, Peca peca) {
        Optional<Peca> existente = buscarPorId(id);
        if (existente.isPresent()) {
            int index = tabelaPecas.indexOf(existente.get());
            peca.setCodigoUnico(id);
            peca.setDataUltimaAtualizacao(LocalDateTime.now());
            tabelaPecas.set(index, peca);
            return Optional.of(peca);
        }
        return Optional.empty();
    }

    public boolean deletar(Long id) {
        return tabelaPecas.removeIf(p -> p.getCodigoUnico().equals(id));
    }
}