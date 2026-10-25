package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.PecaNaoEncontradaException;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.repository.PecaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PecaService {

    private final PecaRepository repository = PecaRepository.getInstance();

    public Peca criar(Peca peca) {
        validar(peca);
        return repository.salvar(peca);
    }

    public List<Peca> buscarTodas() {
        return repository.buscarTodos();
    }

    public Peca buscarPorId(Long id) {
        validarId(id);
        return repository.buscarPorId(id)
                .orElseThrow(() -> new PecaNaoEncontradaException("Peça não encontrada para o ID " + id + "."));
    }

    public Peca atualizar(Long id, Peca peca) {
        validarId(id);
        validar(peca);
        if (repository.buscarPorId(id).isEmpty()) {
            throw new PecaNaoEncontradaException("Peça não encontrada para o ID " + id + ".");
        }
        return repository.atualizar(id, peca)
                .orElseThrow(() -> new PecaNaoEncontradaException("Peça não encontrada para o ID " + id + "."));
    }

    public void deletar(Long id) {
        validarId(id);
        if (!repository.deletar(id)) {
            throw new PecaNaoEncontradaException("Peça não encontrada para o ID " + id + ".");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new DadosInvalidosException("O ID da peça deve ser um número positivo.");
        }
    }

    private void validar(Peca peca) {
        if (peca == null || peca.getNome() == null || peca.getNome().isBlank()) {
            throw new DadosInvalidosException("O nome da peça é obrigatório.");
        }
        if (peca.getPrecoVenda() == null || peca.getPrecoVenda() < 0) {
            throw new DadosInvalidosException("O preço de venda deve ser informado e não pode ser negativo.");
        }
    }
}
