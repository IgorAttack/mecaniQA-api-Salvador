package br.com.mecaniQA.api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoPecas {

    private Long codigoUnico;
    private String fornecedor;
    private StatusPedidoPecas status;
    private Double valorTotal;
    private LocalDateTime dataPedido;
    private LocalDateTime dataRecebimento;
    private LocalDateTime dataUltimaAtualizacao;
    private List<ItemPedidoPeca> itens;

    public PedidoPecas(String fornecedor, StatusPedidoPecas status, Double valorTotal) {
        this.fornecedor = fornecedor;
        this.status = status;
        this.valorTotal = valorTotal;
        this.itens = new ArrayList<>();
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

    public LocalDateTime getDataRecebimento() { return dataRecebimento; }
    public void setDataRecebimento(LocalDateTime dataRecebimento) { this.dataRecebimento = dataRecebimento; }

    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public List<ItemPedidoPeca> getItens() { return itens; }
    public void setItens(List<ItemPedidoPeca> itens) { this.itens = itens; }
}
