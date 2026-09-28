package br.com.mecaniQA.api.dto;

public class ItemPedidoPecaDTO {

    private Long pecaId;
    private Integer quantidade;
    private Double precoUnitario;

    public ItemPedidoPecaDTO() {}

    public ItemPedidoPecaDTO(Long pecaId, Integer quantidade, Double precoUnitario) {
        this.pecaId = pecaId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Long getPecaId() { return pecaId; }
    public void setPecaId(Long pecaId) { this.pecaId = pecaId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }
}