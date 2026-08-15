package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private ServicoRepository repository = ServicoRepository.getInstance();

    @PostMapping
    public ResponseEntity<Servico> cadastrarServico(@RequestBody Servico novoServico) {
        repository.salvar(novoServico);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoServico);
    }

    @GetMapping
    public ResponseEntity<List<Servico>> listarServicos() {
        List<Servico> lista = repository.listarTodos();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }
}