package com.imobiliaria.frontend.model;

import java.util.List;

/** Espelho do JSON de erro (400) devolvido pelo backend: { "mensagem": "...", "detalhes": [...] } */
public record ErroBackend(String mensagem, List<String> detalhes) {
}
