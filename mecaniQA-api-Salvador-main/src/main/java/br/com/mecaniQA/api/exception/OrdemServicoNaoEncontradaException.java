package br.com.mecaniQA.api.exception;

public class OrdemServicoNaoEncontradaException extends RuntimeException {

    public OrdemServicoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}