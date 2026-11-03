package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.ItemPedidoPecaDTO;
import br.com.mecaniQA.api.dto.PedidoPecasDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.PecaNaoEncontradaException;
import br.com.mecaniQA.api.exception.PedidoNaoEncontradoException;
import br.com.mecaniQA.api.service.PedidoPecasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoPecasController {

    private final PedidoPecasService pedidoPecasService;

    public PedidoPecasController(PedidoPecasService pedidoPecasService) {
        this.pedidoPecasService = pedidoPecasService;
    }

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoPecasDTO pedidoDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoPecasService.criarPedido(pedidoDTO));
        } catch (PecaNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PedidoPecasDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoPecasService.listarTodos());
    }

    @GetMapping("/{codigoUnico}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long codigoUnico) {
        try {
            return ResponseEntity.ok(pedidoPecasService.buscarPorId(codigoUnico));
        } catch (PedidoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{codigoUnico}/pecas")
    public ResponseEntity<?> adicionarPecasAoPedido(@PathVariable Long codigoUnico, @RequestBody List<ItemPedidoPecaDTO> novasPecas) {
        try {
            return ResponseEntity.ok(pedidoPecasService.adicionarPecasAoPedido(codigoUnico, novasPecas));
        } catch (PedidoNaoEncontradoException | PecaNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{codigoUnico}/status")
    public ResponseEntity<?> atualizarStatusPedido(@PathVariable Long codigoUnico, @RequestBody PedidoPecasDTO pedidoDTO) {
        try {
            return ResponseEntity.ok(pedidoPecasService.atualizarStatusPedido(codigoUnico, pedidoDTO));
        } catch (PedidoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{codigoUnico}")
    public ResponseEntity<?> atualizarPedido(@PathVariable Long codigoUnico, @RequestBody PedidoPecasDTO pedidoDTO) {
        try {
            return ResponseEntity.ok(pedidoPecasService.atualizarPedido(codigoUnico, pedidoDTO));
        } catch (PedidoNaoEncontradoException | PecaNaoEncontradaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{codigoUnico}")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long codigoUnico) {
        try {
            pedidoPecasService.deletarPedido(codigoUnico);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (PedidoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DadosInvalidosException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}