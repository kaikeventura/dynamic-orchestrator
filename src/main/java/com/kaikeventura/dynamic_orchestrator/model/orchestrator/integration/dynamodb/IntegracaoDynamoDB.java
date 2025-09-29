package com.kaikeventura.dynamic_orchestrator.model.orchestrator.integration.dynamodb;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IntegracaoDynamoDB {
    private String nomeTabela;
    private OperacaoDynamoDB operacao;
    private ParametrosChaveDynamoDB parametrosChave;
    private List<Map<String, Object>> campos;
    private Object resultado;
}
