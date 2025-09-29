package com.kaikeventura.dynamic_orchestrator.model.orchestrator.integration.dynamodb;

import lombok.Data;

import java.util.Map;

@Data
public class ParametrosChaveDynamoDB {
    private Map<String, Object> partitionKey;
    private Map<String, Object> sortKey;
}
