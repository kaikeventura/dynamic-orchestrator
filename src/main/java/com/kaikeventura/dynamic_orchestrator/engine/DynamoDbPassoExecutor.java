package com.kaikeventura.dynamic_orchestrator.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
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
        var integracao = passoDynamoDB.getIntegracao();
        String operacao = integracao.getOperacao().name();

        if ("QUERY".equalsIgnoreCase(operacao)) {
            executarQuery(passoDynamoDB, integracao, variaveis, contexto);
        } else if ("PERSISTENCY".equalsIgnoreCase(operacao)) {
            executarPersistencia(integracao, contexto);
        }
    }

    private void executarQuery(
            FluxoConfig.PassoDynamoDB passo,
            FluxoConfig.IntegracaoDynamoDB integracao,
            List<FluxoConfig.Variavel> variaveis,
            VariavelContexto contexto
    ) {
        var nomeTabela = integracao.getNomeTabela();
        var parametrosChave = integracao.getParametrosChave();
        var partitionKey = parametrosChave.getPartitionKey();
        var resolvedPartitionKey = VariavelSubstituidor.substituir(partitionKey, contexto);

        var partitionKeyName = resolvedPartitionKey.keySet().iterator().next();
        var partitionKeyValue = resolvedPartitionKey.get(partitionKeyName);

        var keyConditionExpression = String.format("%s = :val1", partitionKeyName);
        var expressionAttributeValues = new HashMap<>(Map.of(
                ":val1", toAttributeValue(partitionKeyValue)
        ));

        var sortKey = parametrosChave.getSortKey();
        var resolvedSortKey = VariavelSubstituidor.substituir(sortKey, contexto);

        if (!resolvedSortKey.isEmpty()) {
            var sortKeyName = resolvedSortKey.keySet().iterator().next();
            var sortKeyValue = resolvedSortKey.get(partitionKeyName);

            keyConditionExpression = String.join(keyConditionExpression, String.format(", %s = :val2", sortKeyName));
            expressionAttributeValues.put(":val2", toAttributeValue(sortKeyValue));
        }

        var queryRequest = QueryRequest.builder()
                .tableName(nomeTabela)
                .keyConditionExpression(keyConditionExpression)
                .expressionAttributeValues(expressionAttributeValues)
                .build();

        var response = dynamoDbClient.query(queryRequest);

        if (response.hasItems() && !response.items().isEmpty()) {
            Map<String, AttributeValue> item = response.items().getFirst();
            Map<String, Object> resultado = fromAttributeValueMap(item);
            salvarResultadoNoContexto(passo, resultado, variaveis, contexto);
        }
    }

    private void executarPersistencia(
            FluxoConfig.IntegracaoDynamoDB integracao,
            VariavelContexto contexto
    ) {
        var nomeTabela = integracao.getNomeTabela();
        var parametrosChave = integracao.getParametrosChave();
        var partitionKey = parametrosChave.getPartitionKey();

        var itemParaPersistir = new HashMap<>(VariavelSubstituidor.substituir(partitionKey, contexto));

        var sortKey = parametrosChave.getSortKey();
        var resolvedSortKey = VariavelSubstituidor.substituir(sortKey, contexto);

        if (!resolvedSortKey.isEmpty()) {
            itemParaPersistir.putAll(VariavelSubstituidor.substituir(sortKey, contexto));
        }

        var campos = integracao.getCampos();

        if (campos != null) {
            for (var campo : campos) {
                itemParaPersistir.putAll(VariavelSubstituidor.substituir(campo, contexto));
            }
        }

        var putItemRequest = PutItemRequest.builder()
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
        for (var variavel : variaveis) {
            if ("orquestrador".equals(variavel.getOrigem().getTipoReferencia())) {
                var idReferencia = variavel.getOrigem().getIdReferencia();
                var pattern = "\\{\\{dynamodb\\$\\.(.+?)\\$\\.resultado\\$\\.(.+?)}}";
                var matcher = java.util.regex.Pattern.compile(pattern).matcher(idReferencia);

                if (matcher.matches()) {
                    String idPassoReferencia = matcher.group(1);
                    String campoResultado = matcher.group(2);

                    if (passo.getId().equals(idPassoReferencia) && resultado.containsKey(campoResultado)) {
                        contexto.set(variavel.getId(), resultado.get(campoResultado));
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
        if (val.n() != null) return Long.parseLong(val.n());
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
            return AttributeValue.builder().m(toAttributeValueMap((Map<String, Object>) value)).build();
        }
        return AttributeValue.builder().nul(true).build();
    }
}
