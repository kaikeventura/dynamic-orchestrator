package com.kaikeventura.dynamic_orchestrator.service;

import com.kaikeventura.dynamic_orchestrator.engine.FluxoEngine;
import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import com.kaikeventura.dynamic_orchestrator.registry.FluxoRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FluxoService {
    private final FluxoRegistry registry;
    private final FluxoEngine engine;

    public Map<String, Object> executarFluxo(String fluxo, Map<String, Object> entrada) {
        FluxoConfig config = registry.getFluxo(fluxo);
        if (config == null) {
            throw new IllegalArgumentException("Fluxo não encontrado: " + fluxo);
        }
        return engine.executar(config, entrada);
    }
}
