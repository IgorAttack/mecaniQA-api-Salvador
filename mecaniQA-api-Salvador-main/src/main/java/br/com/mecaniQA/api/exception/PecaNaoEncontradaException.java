package br.com.mecaniQA.api.exception;

public class PecaNaoEncontradaException extends RuntimeException {

    public PecaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
