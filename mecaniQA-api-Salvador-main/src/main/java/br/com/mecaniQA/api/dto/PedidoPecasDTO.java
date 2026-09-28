package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.model.StatusPedidoPecas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoPecasDTO {

    private Long codigoUnico;
    private String fornecedor;
    private StatusPedidoPecas status;
    private Double valorTotal;
    private LocalDateTime dataPedido;
    private List<ItemPedidoPecaDTO> itens;

    public PedidoPecasDTO() {
        this.itens = new ArrayList<>();
    }

    public PedidoPecasDTO(Long codigoUnico, String fornecedor, StatusPedidoPecas status, Double valorTotal, LocalDateTime dataPedido, List<ItemPedidoPecaDTO> itens) {
        this.codigoUnico = codigoUnico;
        this.fornecedor = fornecedor;
        this.status = status;
        this.valorTotal = valorTotal;
        this.dataPedido = dataPedido;
        this.itens = itens != null ? itens : new ArrayList<>();
    }

    public Long getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(Long codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }

    public StatusPedidoPecas getStatus() { return status; }
    public void setStatus(StatusPedidoPecas status) { this.status = status; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }

    public List<ItemPedidoPecaDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoPecaDTO> itens) { this.itens = itens; }
}