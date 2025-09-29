package com.kaikeventura.dynamic_orchestrator.model.contract;

import lombok.Data;

import java.util.List;

@Data
public class Contrato {
    private List<CampoContrato> entrada;
    private List<CampoContratoSaida> saida;
}
