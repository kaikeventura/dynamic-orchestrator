package com.kaikeventura.dynamic_orchestrator.transform;

import com.kaikeventura.dynamic_orchestrator.engine.VariavelContexto;

public interface Transformador {
    String getNome();
    Object transformar(String[] parametros, VariavelContexto contexto);
}
