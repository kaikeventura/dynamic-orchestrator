package com.kaikeventura.dynamic_orchestrator.model.orchestrator.step;

import com.kaikeventura.dynamic_orchestrator.model.orchestrator.integration.api.IntegracaoAPI;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PassoAPI extends PassoBase {
    private IntegracaoAPI integracao;
}
