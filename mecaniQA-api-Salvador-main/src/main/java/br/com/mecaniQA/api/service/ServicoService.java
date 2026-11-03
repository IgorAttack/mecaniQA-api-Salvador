package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.ServicoNaoEncontradoException;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository repository = ServicoRepository.getInstance();

    public Servico criar(Servico servico) {
        validar(servico);
        return repository.salvar(servico);
    }

    public List<Servico> buscarTodos() {
        return repository.buscarTodos();
    }

    public Servico buscarPorId(Long id) {
        validarId(id);
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado para o ID " + id + "."));
    }

    public Servico atualizar(Long id, Servico servico) {
        validarId(id);
        validar(servico);
        if (repository.buscarPorId(id).isEmpty()) {
            throw new ServicoNaoEncontradoException("Serviço não encontrado para o ID " + id + ".");
        }
        return repository.atualizar(id, servico)
                .orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado para o ID " + id + "."));
    }

    public void deletar(Long id) {
        validarId(id);
        if (!repository.deletar(id)) {
            throw new ServicoNaoEncontradoException("Serviço não encontrado para o ID " + id + ".");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new DadosInvalidosException("O ID do serviço deve ser um número positivo.");
        }
    }

    private void validar(Servico servico) {
        if (servico == null || servico.getNomeServico() == null || servico.getNomeServico().isBlank()) {
            throw new DadosInvalidosException("O nome do serviço é obrigatório.");
        }
        if (servico.getCustoTabelado() == null || servico.getCustoTabelado() < 0) {
            throw new DadosInvalidosException("O custo tabelado do serviço deve ser informado e não pode ser negativo.");
        }
    }
}