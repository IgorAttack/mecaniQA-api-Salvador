package br.com.mecaniQA.api.model;

import java.time.LocalDateTime;

public class Servico {

    private Long codigoUnico;
    private String nomeServico;
    private Integer tempoEstimadoMinutos;
    private Double custoTabelado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;

    public Servico(String nomeServico, Integer tempoEstimadoMinutos, Double custoTabelado) {
        this.nomeServico = nomeServico;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.custoTabelado = custoTabelado;
    }

    public Long getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(Long codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getNomeServico() { return nomeServico; }
    public void setNomeServico(String nomeServico) { this.nomeServico = nomeServico; }

    public Integer getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }
    public void setTempoEstimadoMinutos(Integer tempoEstimadoMinutos) { this.tempoEstimadoMinutos = tempoEstimadoMinutos; }

    public Double getCustoTabelado() { return custoTabelado; }
    public void setCustoTabelado(Double custoTabelado) { this.custoTabelado = custoTabelado; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }
}