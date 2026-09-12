package br.com.mecaniQA.api.model;

public class ItemPedidoPeca {

    private Long codigoUnico;
    private PedidoPecas pedidoPecas;
    private Peca peca;
    private Integer quantidade;
    private Double precoUnitario;

    public ItemPedidoPeca(PedidoPecas pedidoPecas, Peca peca, Integer quantidade, Double precoUnitario) {
        this.pedidoPecas = pedidoPecas;
        this.peca = peca;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Long getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(Long codigoUnico) { this.codigoUnico = codigoUnico; }

    public PedidoPecas getPedidoPecas() { return pedidoPecas; }
    public void setPedidoPecas(PedidoPecas pedidoPecas) { this.pedidoPecas = pedidoPecas; }

    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }
}
