package br.com.mecaniQA.api.controller;

import br.com.mecaniQA.api.dto.OrdemServicoDTO;
import br.com.mecaniQA.api.mapper.OrdemServicoMapper;
import br.com.mecaniQA.api.model.OrdemServico;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordens-servico")
public class OrdemServicoController {


    @PostMapping
    public ResponseEntity<OrdemServicoDTO> criarOS(@RequestBody OrdemServicoDTO osDTO) {
        OrdemServico novaOS = OrdemServicoMapper.toEntity(osDTO);
        OrdemServicoDTO respostaDTO = OrdemServicoMapper.toDTO(novaOS);
        return ResponseEntity.status(HttpStatus.CREATED).body(respostaDTO);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrdemServicoDTO> atualizarStatus(@PathVariable Long id, @RequestBody OrdemServicoDTO osDTO) {

        OrdemServico osExistente = OrdemServicoMapper.toEntity(osDTO);
        osExistente.setStatus(osDTO.getStatus());
        return ResponseEntity.ok(OrdemServicoMapper.toDTO(osExistente));
    }
}