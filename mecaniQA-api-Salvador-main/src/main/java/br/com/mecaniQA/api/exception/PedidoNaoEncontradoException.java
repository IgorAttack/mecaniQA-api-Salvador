package br.com.mecaniQA.api.exception;

public class PedidoNaoEncontradoException extends RuntimeException {

    public PedidoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}