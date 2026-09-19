package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.model.StatusOrdemServico;
import java.time.LocalDateTime;

public class OrdemServicoDTO {

    private Long codigoUnico;
    private String descricaoProblema;
    private StatusOrdemServico status;
    private Double valorTotal;
    private LocalDateTime dataAbertura;

    public OrdemServicoDTO() {}

    public OrdemServicoDTO(Long codigoUnico, String descricaoProblema, StatusOrdemServico status, Double valorTotal, LocalDateTime dataAbertura) {
        this.codigoUnico = codigoUnico;
        this.descricaoProblema = descricaoProblema;
        this.status = status;
        this.valorTotal = valorTotal;
        this.dataAbertura = dataAbertura;
    }

    // Getters e Setters manuais
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
}