package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.OrdemServicoDTO;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.PedidoPecasRepository;
import br.com.mecaniQA.api.repository.ServicoRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrdemServicoMapper {

    private OrdemServicoMapper() {}

    public static OrdemServico toEntity(OrdemServicoDTO dto) {
        if (dto == null) return null;

        // Recuperando as listas do repositório em memória baseadas nos IDs passados
        List<Servico> servicos = dto.getServicosIds().stream()
                .map(id -> ServicoRepository.getInstance().buscarPorId(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<PedidoPecas> pedidos = dto.getPedidosPecasIds().stream()
                .map(id -> PedidoPecasRepository.getInstance().buscarPorId(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Utilizando o Padrão Builder obrigatório na OAT
        return OrdemServico.builder()
                .codigoUnico(dto.getCodigoUnico())
                .descricaoProblema(dto.getDescricaoProblema())
                .status(dto.getStatus())
                .valorTotal(dto.getValorTotal())
                .dataAbertura(dto.getDataAbertura())
                .servicos(servicos)
                .pedidosPecas(pedidos)
                .build();
    }

    public static OrdemServicoDTO toDTO(OrdemServico entity) {
        if (entity == null) return null;

        OrdemServicoDTO dto = new OrdemServicoDTO();
        dto.setCodigoUnico(entity.getCodigoUnico());
        dto.setDescricaoProblema(entity.getDescricaoProblema());
        dto.setStatus(entity.getStatus());
        dto.setValorTotal(entity.getValorTotal());
        dto.setDataAbertura(entity.getDataAbertura());

        if (entity.getServicos() != null) {
            dto.setServicosIds(entity.getServicos().stream()
                    .map(Servico::getCodigoUnico)
                    .collect(Collectors.toList()));
        }

        if (entity.getPedidosPecas() != null) {
            dto.setPedidosPecasIds(entity.getPedidosPecas().stream()
                    .map(PedidoPecas::getCodigoUnico)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}