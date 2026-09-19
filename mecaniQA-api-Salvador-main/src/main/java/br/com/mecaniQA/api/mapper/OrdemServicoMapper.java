package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.OrdemServicoDTO;
import br.com.mecaniQA.api.model.OrdemServico;

public class OrdemServicoMapper {

    private OrdemServicoMapper() {}

    public static OrdemServico toEntity(OrdemServicoDTO dto) {
        if (dto == null) {
            return null;
        }

        return OrdemServico.builder()
                .codigoUnico(dto.getCodigoUnico())
                .descricaoProblema(dto.getDescricaoProblema())
                .status(dto.getStatus())
                .valorTotal(dto.getValorTotal())
                .dataAbertura(dto.getDataAbertura())
                .build();
    }

    public static OrdemServicoDTO toDTO(OrdemServico entity) {
        if (entity == null) {
            return null;
        }

        return new OrdemServicoDTO(
                entity.getCodigoUnico(),
                entity.getDescricaoProblema(),
                entity.getStatus(),
                entity.getValorTotal(),
                entity.getDataAbertura()
        );
    }
}