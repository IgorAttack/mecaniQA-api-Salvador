package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoRepository servicoRepository = ServicoRepository.getInstance();

    @PostMapping
    public ResponseEntity<Servico> criarServico(@RequestBody Servico servico) {
        Servico servicoSalvo = servicoRepository.salvar(servico);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoSalvo); // Retorna 201 Created
    }


    @GetMapping
    public ResponseEntity<List<Servico>> listarTodos() {
        return ResponseEntity.ok(servicoRepository.buscarTodos()); // Retorna 200 OK
    }


    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        return servicoRepository.buscarPorId(id)
                .map(ResponseEntity::ok) // Retorna 200 OK se encontrar
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Retorna 404 Not Found
    }


    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizarServico(@PathVariable Long id, @RequestBody Servico servico) {
        return servicoRepository.atualizar(id, servico)
                .map(ResponseEntity::ok) // Retorna 200 OK
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Retorna 404 Not Found
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarServico(@PathVariable Long id) {
        if (servicoRepository.deletar(id)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Retorna 204 No Content
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 Not Found
        }
    }
}