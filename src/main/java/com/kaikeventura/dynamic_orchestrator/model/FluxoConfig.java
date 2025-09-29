package com.kaikeventura.dynamic_orchestrator.model;

import com.kaikeventura.dynamic_orchestrator.model.contract.Contrato;
import com.kaikeventura.dynamic_orchestrator.model.metadata.Metadados;
import com.kaikeventura.dynamic_orchestrator.model.orchestrator.Orquestrador;
import lombok.Data;

@Data
public class FluxoConfig {
    private String fluxo;
    private Contrato contrato;
    private Metadados metadados;
    private Orquestrador orquestrador;
}
