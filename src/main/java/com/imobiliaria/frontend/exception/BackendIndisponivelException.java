package com.imobiliaria.frontend.exception;

/** O backend está fora do ar, não respondeu a tempo ou devolveu um erro inesperado. */
public class BackendIndisponivelException extends RuntimeException {

    public BackendIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
