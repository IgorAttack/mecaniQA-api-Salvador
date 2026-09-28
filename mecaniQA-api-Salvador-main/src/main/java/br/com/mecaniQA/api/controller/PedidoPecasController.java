package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.ItemPedidoPecaDTO;
import br.com.mecaniQA.api.dto.PedidoPecasDTO;
import br.com.mecaniQA.api.mapper.PedidoPecasMapper;
import br.com.mecaniQA.api.model.ItemPedidoPeca;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedidoPecas;
import br.com.mecaniQA.api.repository.PecaRepository;
import br.com.mecaniQA.api.repository.PedidoPecasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoPecasController {

    private final PedidoPecasRepository repository = PedidoPecasRepository.getInstance();
    private final PecaRepository pecaRepository = PecaRepository.getInstance();

    @PostMapping
    public ResponseEntity<PedidoPecasDTO> criarPedido(@RequestBody PedidoPecasDTO pedidoDTO) {
        PedidoPecas novoPedido = PedidoPecasMapper.toEntity(pedidoDTO);
        novoPedido.setStatus(StatusPedidoPecas.ORCANDO);

        PedidoPecas pedidoSalvo = repository.salvar(novoPedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoPecasMapper.toDTO(pedidoSalvo));
    }

    @PostMapping("/{codigoUnico}/pecas")
    public ResponseEntity<PedidoPecasDTO> adicionarPecasAoPedido(@PathVariable Long codigoUnico, @RequestBody List<ItemPedidoPecaDTO> novasPecas) {
        Optional<PedidoPecas> pedidoOpt = repository.buscarPorId(codigoUnico);

        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        PedidoPecas pedidoExistente = pedidoOpt.get();

        novasPecas.forEach(dto -> {
            Peca pecaReal = pecaRepository.buscarPorId(dto.getPecaId()).orElse(null);
            ItemPedidoPeca novoItem = new ItemPedidoPeca(pedidoExistente, pecaReal, dto.getQuantidade(), dto.getPrecoUnitario());
            pedidoExistente.getItens().add(novoItem);
        });

        repository.salvar(pedidoExistente);
        return ResponseEntity.ok(PedidoPecasMapper.toDTO(pedidoExistente));
    }

    @PutMapping("/{codigoUnico}/status")
    public ResponseEntity<PedidoPecasDTO> atualizarStatusPedido(@PathVariable Long codigoUnico, @RequestBody PedidoPecasDTO pedidoDTO) {
        return repository.buscarPorId(codigoUnico).map(pedidoExistente -> {
            pedidoExistente.setStatus(pedidoDTO.getStatus());
            repository.salvar(pedidoExistente);
            return ResponseEntity.ok(PedidoPecasMapper.toDTO(pedidoExistente));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}