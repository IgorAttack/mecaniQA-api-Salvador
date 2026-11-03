package br.com.mecaniQA.api.exception;

public class ServicoNaoEncontradoException extends RuntimeException {

    public ServicoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}