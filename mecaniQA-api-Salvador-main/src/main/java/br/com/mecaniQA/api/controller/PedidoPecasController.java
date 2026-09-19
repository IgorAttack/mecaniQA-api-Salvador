package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.ItemPedidoPecaDTO;
import br.com.mecaniQA.api.dto.PedidoPecasDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoPecasController {

    @PostMapping
    public ResponseEntity<PedidoPecasDTO> criarPedido(@RequestBody PedidoPecasDTO pedidoDTO) {
        pedidoDTO.setStatus("Orçando");


        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTO);
    }

    @PostMapping("/{id}/pecas")
    public ResponseEntity<PedidoPecasDTO> adicionarPecasAoPedido(@PathVariable Long id, @RequestBody List<ItemPedidoPecaDTO> novasPecas) {
        PedidoPecasDTO pedidoSimulado = new PedidoPecasDTO();
        pedidoSimulado.setId(id);

        pedidoSimulado.getItens().addAll(novasPecas);

        return ResponseEntity.ok(pedidoSimulado);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoPecasDTO> atualizarStatusPedido(@PathVariable Long id, @RequestBody PedidoPecasDTO pedidoDTO) {
        PedidoPecasDTO pedidoSimulado = new PedidoPecasDTO();
        pedidoSimulado.setId(id);

        pedidoSimulado.setStatus(pedidoDTO.getStatus());

        return ResponseEntity.ok(pedidoSimulado);
    }
}