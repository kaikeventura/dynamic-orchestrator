package com.kaikeventura.dynamic_orchestrator.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("DYNAMODB")
@RequiredArgsConstructor
public class DynamoDbPassoExecutor implements PassoExecutor {

    private final ObjectMapper objectMapper;
    private final DynamoDbClient dynamoDbClient;

    @Override
    public boolean suporta(String tipo) {
        return "DYNAMODB".equalsIgnoreCase(tipo);
    }

    @Override
    public void executarPasso(FluxoConfig.PassoBase passo, List<FluxoConfig.Variavel> variaveis, VariavelContexto contexto) {
        var passoDynamoDB = (FluxoConfig.PassoDynamoDB) passo;
        Map<String, Object> integracaoMap = objectMapper.convertValue(passoDynamoDB.getIntegracao(), Map.class);
        String operacao = (String) integracaoMap.get("operacao");

        if ("QUERY".equalsIgnoreCase(operacao)) {
            executarQuery(passoDynamoDB, integracaoMap, variaveis, contexto);
        } else if ("PERSISTENCY".equalsIgnoreCase(operacao)) {
            executarPersistencia(passoDynamoDB, integracaoMap, contexto);
        }
    }

    private void executarQuery(
            FluxoConfig.PassoDynamoDB passo,
            Map<String, Object> integracaoMap,
            List<FluxoConfig.Variavel> variaveis,
            VariavelContexto contexto
    ) {
        String nomeTabela = (String) integracaoMap.get("nomeTabela");
        Map<String, Object> parametrosChave = (Map<String, Object>) integracaoMap.get("parametrosChave");
        Map<String, Object> partitionKey = (Map<String, Object>) parametrosChave.get("partitionKey");
        Map<String, Object> resolvedPartitionKey = VariavelSubstituidor.substituir(partitionKey, contexto);

        String keyName = resolvedPartitionKey.keySet().iterator().next();
        Object keyValue = resolvedPartitionKey.get(keyName);

        String keyConditionExpression = String.format("%s = :val", keyName);
        Map<String, AttributeValue> expressionAttributeValues = Map.of(
                ":val", toAttributeValue(keyValue)
        );

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(nomeTabela)
                .keyConditionExpression(keyConditionExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);

        if (response.hasItems() && !response.items().isEmpty()) {
            Map<String, AttributeValue> item = response.items().get(0);
            Map<String, Object> resultado = fromAttributeValueMap(item);
            salvarResultadoNoContexto(passo, resultado, variaveis, contexto);
        }
    }

    private void executarPersistencia(
            FluxoConfig.PassoDynamoDB passo,
            Map<String, Object> integracaoMap,
            VariavelContexto contexto
    ) {
        String nomeTabela = (String) integracaoMap.get("nomeTabela");
        Map<String, Object> parametrosChave = (Map<String, Object>) integracaoMap.get("parametrosChave");
        List<Map<String, Object>> campos = (List<Map<String, Object>>) integracaoMap.get("campos");

        Map<String, Object> itemParaPersistir = new HashMap<>();

        Map<String, Object> partitionKey = (Map<String, Object>) parametrosChave.get("partitionKey");
        itemParaPersistir.putAll(VariavelSubstituidor.substituir(partitionKey, contexto));

        if (parametrosChave.get("sortKey") != null) {
            Map<String, Object> sortKey = (Map<String, Object>) parametrosChave.get("sortKey");
            itemParaPersistir.putAll(VariavelSubstituidor.substituir(sortKey, contexto));
        }

        if (campos != null) {
            for (Map<String, Object> campo : campos) {
                itemParaPersistir.putAll(VariavelSubstituidor.substituir(campo, contexto));
            }
        }

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(nomeTabela)
                .item(toAttributeValueMap(itemParaPersistir))
                .build();

        dynamoDbClient.putItem(putItemRequest);
    }

    private void salvarResultadoNoContexto(
            FluxoConfig.PassoDynamoDB passo,
            Map<String, Object> resultado,
            List<FluxoConfig.Variavel> variaveis,
            VariavelContexto contexto
    ) {
        for (FluxoConfig.Variavel var : variaveis) {
            if ("orquestrador".equals(var.getOrigem().getTipoReferencia())) {
                String idReferencia = var.getOrigem().getIdReferencia();
                String pattern = "\\{\\{dynamodb\\$\\.(.+?)\\$\\.resultado\\$\\.(.+?)}}";
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(idReferencia);

                if (matcher.matches()) {
                    String idPassoReferencia = matcher.group(1);
                    String campoResultado = matcher.group(2);

                    if (passo.getId().equals(idPassoReferencia) && resultado.containsKey(campoResultado)) {
                        contexto.set(var.getId(), resultado.get(campoResultado));
                    }
                }
            }
        }
    }

    private Map<String, Object> fromAttributeValueMap(Map<String, AttributeValue> awsMap) {
        return awsMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> fromAttributeValue(e.getValue())));
    }

    private Object fromAttributeValue(AttributeValue val) {
        if (val.s() != null) return val.s();
        if (val.n() != null) return Long.parseLong(val.n()); // Simplificado para Long
        if (val.bool() != null) return val.bool();
        if (val.hasM()) return fromAttributeValueMap(val.m());
        return null;
    }

    private Map<String, AttributeValue> toAttributeValueMap(Map<String, Object> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toAttributeValue(e.getValue())));
    }

    private AttributeValue toAttributeValue(Object value) {
        if (value instanceof String) {
            return AttributeValue.builder().s((String) value).build();
        }
        if (value instanceof Number) {
            return AttributeValue.builder().n(value.toString()).build();
        }
        if (value instanceof Boolean) {
            return AttributeValue.builder().bool((Boolean) value).build();
        }
        if (value instanceof Map) {
            //noinspection unchecked
            return AttributeValue.builder().m(toAttributeValueMap((Map<String, Object>) value)).build();
        }
        return AttributeValue.builder().nul(true).build();
    }
}
