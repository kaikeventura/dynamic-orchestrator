package com.kaikeventura.dynamic_orchestrator.engine;

import com.kaikeventura.dynamic_orchestrator.model.metadata.Variavel;
import com.kaikeventura.dynamic_orchestrator.model.orchestrator.step.PassoBase;

import java.util.List;

public interface PassoExecutor {
    boolean suporta(String tipo);
    void executarPasso(PassoBase passo, List<Variavel> variaveis, VariavelContexto contexto) throws Exception;
}
