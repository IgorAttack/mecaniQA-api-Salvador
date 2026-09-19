package br.com.mecaniQA.api.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoPecasDTO {

    private Long id;
    private String status;
    private LocalDateTime dataCriacao;
    private List<ItemPedidoPecaDTO> itens;

    public PedidoPecasDTO() {
        this.itens = new ArrayList<>();
    }

    public PedidoPecasDTO(Long id, String status, LocalDateTime dataCriacao, List<ItemPedidoPecaDTO> itens) {
        this.id = id;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.itens = itens != null ? itens : new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public List<ItemPedidoPecaDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoPecaDTO> itens) { this.itens = itens; }
}