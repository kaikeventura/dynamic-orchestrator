package com.kaikeventura.dynamic_orchestrator.model.orchestrator.integration.api;

import lombok.Data;

import java.util.Map;

@Data
public class Requisicao {
    private String url;
    private String metodo;
    private Map<String, Object> headers;
    private Map<String, Object> queryParams;
    private Map<String, Object> body;
}
