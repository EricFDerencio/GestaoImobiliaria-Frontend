package com.imobiliaria.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imobiliaria.frontend.exception.BackendIndisponivelException;
import com.imobiliaria.frontend.exception.DadosRejeitadosException;
import com.imobiliaria.frontend.model.ErroBackend;
import com.imobiliaria.frontend.model.Imovel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Único ponto de contato com o backend: faz as chamadas HTTP/JSON e
 * traduz as falhas em exceções simples para o controller tratar.
 */
@Service
public class ImovelService {

    private static final String MSG_FORA_DO_AR =
            "Não foi possível conectar ao servidor de imóveis. Verifique se o backend está no ar e tente novamente.";
    private static final String MSG_ERRO_INESPERADO =
            "O servidor de imóveis retornou um erro inesperado. Tente novamente em instantes.";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ImovelService(@Value("${backend.url}") String backendUrl, ObjectMapper objectMapper) {
        // Timeouts evitam que a página fique travada se o backend não responder
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);

        this.restClient = RestClient.builder()
                .baseUrl(backendUrl)
                .requestFactory(factory)
                .build();
        this.objectMapper = objectMapper;
    }

    /** GET /api/imoveis */
    public List<Imovel> listar() {
        try {
            List<Imovel> imoveis = restClient.get()
                    .uri("/api/imoveis")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Imovel>>() { });
            return imoveis == null ? List.of() : imoveis;
        } catch (ResourceAccessException e) {
            throw new BackendIndisponivelException(MSG_FORA_DO_AR, e);
        } catch (RestClientException e) {
            throw new BackendIndisponivelException(MSG_ERRO_INESPERADO, e);
        }
    }

    /** POST /api/imoveis */
    public Imovel cadastrar(Imovel imovel) {
        try {
            return restClient.post()
                    .uri("/api/imoveis")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(imovel)
                    .retrieve()
                    .body(Imovel.class);
        } catch (HttpClientErrorException.BadRequest e) {
            throw converterErro400(e);
        } catch (ResourceAccessException e) {
            throw new BackendIndisponivelException(MSG_FORA_DO_AR, e);
        } catch (RestClientException e) {
            throw new BackendIndisponivelException(MSG_ERRO_INESPERADO, e);
        }
    }

    // Lê o JSON de erro do backend; se não conseguir, usa uma mensagem genérica
    private DadosRejeitadosException converterErro400(HttpClientErrorException e) {
        try {
            ErroBackend erro = objectMapper.readValue(e.getResponseBodyAsString(), ErroBackend.class);
            String mensagem = erro.mensagem() != null ? erro.mensagem() : "Dados inválidos";
            List<String> detalhes = erro.detalhes() != null ? erro.detalhes() : List.of();
            return new DadosRejeitadosException(mensagem, detalhes);
        } catch (Exception ignorada) {
            return new DadosRejeitadosException("O servidor rejeitou os dados enviados.", List.of());
        }
    }
}
