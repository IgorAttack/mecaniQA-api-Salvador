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


    private OrdemServico(Builder builder) {
        this.codigoUnico = builder.codigoUnico;
        this.descricaoProblema = builder.descricaoProblema;
        this.status = builder.status;
        this.valorTotal = builder.valorTotal;
        this.dataAbertura = builder.dataAbertura;
        this.dataFechamento = builder.dataFechamento;
        this.dataUltimaAtualizacao = builder.dataUltimaAtualizacao;
        this.servicos = builder.servicos;
        this.pedidosPecas = builder.pedidosPecas;
    }


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private Long codigoUnico;
        private String descricaoProblema;
        private StatusOrdemServico status;
        private Double valorTotal;
        private LocalDateTime dataAbertura;
        private LocalDateTime dataFechamento;
        private LocalDateTime dataUltimaAtualizacao;
        private List<Servico> servicos = new ArrayList<>();
        private List<PedidoPecas> pedidosPecas = new ArrayList<>();

        public Builder codigoUnico(Long codigoUnico) {
            this.codigoUnico = codigoUnico;
            return this;
        }

        public Builder descricaoProblema(String descricaoProblema) {
            this.descricaoProblema = descricaoProblema;
            return this;
        }

        public Builder status(StatusOrdemServico status) {
            this.status = status;
            return this;
        }

        public Builder valorTotal(Double valorTotal) {
            this.valorTotal = valorTotal;
            return this;
        }

        public Builder dataAbertura(LocalDateTime dataAbertura) {
            this.dataAbertura = dataAbertura;
            return this;
        }

        public Builder dataFechamento(LocalDateTime dataFechamento) {
            this.dataFechamento = dataFechamento;
            return this;
        }

        public Builder dataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
            this.dataUltimaAtualizacao = dataUltimaAtualizacao;
            return this;
        }

        public Builder servicos(List<Servico> servicos) {
            this.servicos = servicos;
            return this;
        }

        public Builder pedidosPecas(List<PedidoPecas> pedidosPecas) {
            this.pedidosPecas = pedidosPecas;
            return this;
        }

        public OrdemServico build() {
            return new OrdemServico(this);
        }
    }



    public Long getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(Long codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

    public List<PedidoPecas> getPedidosPecas() {
        return pedidosPecas;
    }

    public void setPedidosPecas(List<PedidoPecas> pedidosPecas) {
        this.pedidosPecas = pedidosPecas;
    }
}