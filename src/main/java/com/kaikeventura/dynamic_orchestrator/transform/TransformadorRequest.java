package com.kaikeventura.dynamic_orchestrator.transform;

import com.kaikeventura.dynamic_orchestrator.engine.VariavelContexto;
import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;

public record TransformadorRequest(
        String[] parametros,
        FluxoConfig config,
        VariavelContexto contexto
) {
}
