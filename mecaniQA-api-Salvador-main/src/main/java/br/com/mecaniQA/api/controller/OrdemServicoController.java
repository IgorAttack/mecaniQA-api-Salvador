package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.OrdemServicoDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.OrdemServicoNaoEncontradaException;
import br.com.mecaniQA.api.service.OrdemServicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @PostMapping
    public ResponseEntity<?> criarOS(@RequestBody OrdemServicoDTO osDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ordemServicoService.criarOS(osDTO));
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<OrdemServicoDTO>> listarTodas() {
        return ResponseEntity.ok(ordemServicoService.listarTodas());
    }

    @GetMapping("/{codigoUnico}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long codigoUnico) {
        try {
            return ResponseEntity.ok(ordemServicoService.buscarPorId(codigoUnico));
        } catch (OrdemServicoNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{codigoUnico}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long codigoUnico, @RequestBody OrdemServicoDTO osDTO) {
        try {
            return ResponseEntity.ok(ordemServicoService.atualizarStatus(codigoUnico, osDTO));
        } catch (OrdemServicoNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{codigoUnico}")
    public ResponseEntity<?> atualizarOS(@PathVariable Long codigoUnico, @RequestBody OrdemServicoDTO osDTO) {
        try {
            return ResponseEntity.ok(ordemServicoService.atualizarOS(codigoUnico, osDTO));
        } catch (OrdemServicoNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{codigoUnico}")
    public ResponseEntity<Void> deletarOS(@PathVariable Long codigoUnico) {
        try {
            ordemServicoService.deletarOS(codigoUnico);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (OrdemServicoNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}