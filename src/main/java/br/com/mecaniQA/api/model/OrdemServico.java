package br.com.mecaniQA.api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdemServico {

    private Long codigoUnico;
    private String descricaoProblema;
    private StatusOrdemServico status;
    private Double valorTotal;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private LocalDateTime dataUltimaAtualizacao;
    private List<Servico> servicos;
    private List<PedidoPecas> pedidosPecas;

    public OrdemServico(String descricaoProblema, StatusOrdemServico status, Double valorTotal) {
        this.descricaoProblema = descricaoProblema;
        this.status = status;
        this.valorTotal = valorTotal;
        this.servicos = new ArrayList<>();
        this.pedidosPecas = new ArrayList<>();
    }

    public Long getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(Long codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getDescricaoProblema() { return descricaoProblema; }
    public void setDescricaoProblema(String descricaoProblema) { this.descricaoProblema = descricaoProblema; }

    public StatusOrdemServico getStatus() { return status; }
    public void setStatus(StatusOrdemServico status) { this.status = status; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }

    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public List<Servico> getServicos() { return servicos; }
    public void setServicos(List<Servico> servicos) { this.servicos = servicos; }

    public List<PedidoPecas> getPedidosPecas() { return pedidosPecas; }
    public void setPedidosPecas(List<PedidoPecas> pedidosPecas) { this.pedidosPecas = pedidosPecas; }
}
