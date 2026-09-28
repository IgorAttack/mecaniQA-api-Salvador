package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.ItemPedidoPecaDTO;
import br.com.mecaniQA.api.dto.PedidoPecasDTO;
import br.com.mecaniQA.api.model.ItemPedidoPeca;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.repository.PecaRepository;

import java.util.stream.Collectors;

public class PedidoPecasMapper {

    private PedidoPecasMapper() {}

    public static PedidoPecas toEntity(PedidoPecasDTO dto) {
        if (dto == null) return null;

        PedidoPecas entidade = new PedidoPecas(dto.getFornecedor(), dto.getStatus(), dto.getValorTotal());
        entidade.setCodigoUnico(dto.getCodigoUnico());
        entidade.setDataPedido(dto.getDataPedido());

        if (dto.getItens() != null) {
            entidade.setItens(dto.getItens().stream().map(itemDTO -> {
                // Busca a peça real do repositório
                Peca peca = PecaRepository.getInstance().buscarPorId(itemDTO.getPecaId()).orElse(null);
                return new ItemPedidoPeca(entidade, peca, itemDTO.getQuantidade(), itemDTO.getPrecoUnitario());
            }).collect(Collectors.toList()));
        }

        return entidade;
    }

    public static PedidoPecasDTO toDTO(PedidoPecas entity) {
        if (entity == null) return null;

        PedidoPecasDTO dto = new PedidoPecasDTO(
                entity.getCodigoUnico(),
                entity.getFornecedor(),
                entity.getStatus(),
                entity.getValorTotal(),
                entity.getDataPedido(),
                null
        );

        if (entity.getItens() != null) {
            dto.setItens(entity.getItens().stream().map(item ->
                    new ItemPedidoPecaDTO(
                            item.getPeca() != null ? item.getPeca().getCodigoUnico() : null,
                            item.getQuantidade(),
                            item.getPrecoUnitario()
                    )
            ).collect(Collectors.toList()));
        }

        return dto;
    }
}