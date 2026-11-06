package com.imobiliaria.frontend.exception;

import java.util.List;

/** O backend respondeu 400: os dados enviados foram rejeitados. Carrega os detalhes para exibir ao usuário. */
public class DadosRejeitadosException extends RuntimeException {

    private final List<String> detalhes;

    public DadosRejeitadosException(String mensagem, List<String> detalhes) {
        super(mensagem);
        this.detalhes = detalhes;
    }

    public List<String> getDetalhes() {
        return detalhes;
    }
}
