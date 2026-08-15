package br.com.mecaniQA.api.model;

import java.time.LocalDateTime;

public class Peca {

    private Long codigoUnico;
    private String nome;
    private String codigoBarras;
    private String fornecedorMarca;
    private Integer quantidadeEstoque;
    private Double precoCusto;
    private Double precoVenda;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataUltimaAtualizacao;
    private CategoriaPeca categoria;
    private String tamanho;
    private String cor;

    public Peca(String nome, String codigoBarras, String fornecedorMarca,
                Integer quantidadeEstoque, Double precoCusto, Double precoVenda,
                CategoriaPeca categoria) {
        this.nome = nome;
        this.codigoBarras = codigoBarras;
        this.fornecedorMarca = fornecedorMarca;
        this.quantidadeEstoque = quantidadeEstoque;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.categoria = categoria;
    }

    public Long getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(Long codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getFornecedorMarca() { return fornecedorMarca; }
    public void setFornecedorMarca(String fornecedorMarca) { this.fornecedorMarca = fornecedorMarca; }

    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public Double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(Double precoCusto) { this.precoCusto = precoCusto; }

    public Double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(Double precoVenda) { this.precoVenda = precoVenda; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }

    public CategoriaPeca getCategoria() { return categoria; }
    public void setCategoria(CategoriaPeca categoria) { this.categoria = categoria; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
}