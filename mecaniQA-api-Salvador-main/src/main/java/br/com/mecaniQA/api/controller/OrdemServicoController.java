package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.OrdemServicoDTO;
import br.com.mecaniQA.api.mapper.OrdemServicoMapper;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.StatusOrdemServico;
import br.com.mecaniQA.api.repository.OrdemServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoRepository repository = OrdemServicoRepository.getInstance();

    @PostMapping
    public ResponseEntity<OrdemServicoDTO> criarOS(@RequestBody OrdemServicoDTO osDTO) {
        OrdemServico novaOS = OrdemServicoMapper.toEntity(osDTO);
        novaOS.setStatus(StatusOrdemServico.ABERTO); // Status padrão na criação

        OrdemServico osSalva = repository.salvar(novaOS);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrdemServicoMapper.toDTO(osSalva));
    }

    @PutMapping("/{codigoUnico}/status")
    public ResponseEntity<OrdemServicoDTO> atualizarStatus(@PathVariable Long codigoUnico, @RequestBody OrdemServicoDTO osDTO) {
        return repository.buscarPorId(codigoUnico).map(osExistente -> {
            osExistente.setStatus(osDTO.getStatus());
            repository.salvar(osExistente);
            return ResponseEntity.ok(OrdemServicoMapper.toDTO(osExistente));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}