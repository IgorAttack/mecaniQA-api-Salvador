package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.repository.PecaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pecas")
public class PecaController {

    private PecaRepository repository = PecaRepository.getInstance();

    @PostMapping
    public ResponseEntity<Peca> cadastrarPeca(@RequestBody Peca novaPeca) {
        repository.salvar(novaPeca);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPeca);
    }

    @GetMapping
    public ResponseEntity<List<Peca>> listarPecas() {
        List<Peca> lista = repository.listarTodas();
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }
}