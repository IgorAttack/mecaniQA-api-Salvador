package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.model.StatusOrdemServico;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdemServicoDTO {

    private Long codigoUnico;
    private String descricaoProblema;
    private StatusOrdemServico status;
    private Double valorTotal;
    private LocalDateTime dataAbertura;
    private List<Long> servicosIds;
    private List<Long> pedidosPecasIds;

    public OrdemServicoDTO() {
        this.servicosIds = new ArrayList<>();
        this.pedidosPecasIds = new ArrayList<>();
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

    public List<Long> getServicosIds() { return servicosIds; }
    public void setServicosIds(List<Long> servicosIds) { this.servicosIds = servicosIds; }

    public List<Long> getPedidosPecasIds() { return pedidosPecasIds; }
    public void setPedidosPecasIds(List<Long> pedidosPecasIds) { this.pedidosPecasIds = pedidosPecasIds; }
}