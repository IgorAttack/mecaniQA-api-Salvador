package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.PecaNaoEncontradaException;
import br.com.mecaniQA.api.service.PecaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pecas")
public class PecaController {

    private final PecaService pecaService;

    public PecaController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    @PostMapping
    public ResponseEntity<?> criarPeca(@RequestBody Peca peca) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pecaService.criar(peca));
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Peca>> listarTodas() {
        return ResponseEntity.ok(pecaService.buscarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pecaService.buscarPorId(id));
        } catch (PecaNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPeca(@PathVariable Long id, @RequestBody Peca peca) {
        try {
            return ResponseEntity.ok(pecaService.atualizar(id, peca));
        } catch (PecaNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPeca(@PathVariable Long id) {
        try {
            pecaService.deletar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (PecaNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}