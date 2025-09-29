package com.kaikeventura.dynamic_orchestrator.model.orchestrator;

import com.kaikeventura.dynamic_orchestrator.model.orchestrator.step.PassoBase;
import lombok.Data;

import java.util.List;

@Data
public class Orquestrador {
    private List<PassoBase> passos;
}
