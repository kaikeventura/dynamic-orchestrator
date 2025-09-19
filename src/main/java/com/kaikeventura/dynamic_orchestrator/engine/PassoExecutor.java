package com.kaikeventura.dynamic_orchestrator.engine;

import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;

import java.util.List;

public interface PassoExecutor {
    boolean suporta(String tipo);
    void executarPasso(FluxoConfig.PassoBase passo, List<FluxoConfig.Variavel> variaveis, VariavelContexto contexto) throws Exception;
}
