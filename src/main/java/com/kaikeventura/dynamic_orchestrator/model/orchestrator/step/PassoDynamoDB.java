package com.kaikeventura.dynamic_orchestrator.model.orchestrator.step;

import com.kaikeventura.dynamic_orchestrator.model.orchestrator.integration.dynamodb.IntegracaoDynamoDB;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PassoDynamoDB extends PassoBase {
    private IntegracaoDynamoDB integracao;
}
