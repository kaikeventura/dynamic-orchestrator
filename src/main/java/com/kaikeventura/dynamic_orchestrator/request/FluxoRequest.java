package com.kaikeventura.dynamic_orchestrator.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class FluxoRequest {
    private String fluxo;
    private Map<String, Object> dadosEntrada;
}
