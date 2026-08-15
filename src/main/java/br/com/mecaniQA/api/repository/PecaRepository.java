package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Peca;
import java.util.ArrayList;
import java.util.List;

public class PecaRepository {

    private static PecaRepository instance;

    private List<Peca> pecas;

    private PecaRepository() {
        this.pecas = new ArrayList<>();
    }

    public static PecaRepository getInstance() {
        if (instance == null) {
            instance = new PecaRepository();
        }
        return instance;
    }

    public void salvar(Peca peca) {
        this.pecas.add(peca);
    }

    public List<Peca> listarTodas() {
        return this.pecas;
    }
}