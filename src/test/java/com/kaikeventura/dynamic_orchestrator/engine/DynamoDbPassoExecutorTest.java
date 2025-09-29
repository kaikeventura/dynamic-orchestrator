package com.kaikeventura.dynamic_orchestrator.engine;

import com.kaikeventura.dynamic_orchestrator.engine.executors.DynamoDbPassoExecutor;
import com.kaikeventura.dynamic_orchestrator.model.common.Origem;
import com.kaikeventura.dynamic_orchestrator.model.metadata.Variavel;
import com.kaikeventura.dynamic_orchestrator.model.orchestrator.integration.dynamodb.IntegracaoDynamoDB;
import com.kaikeventura.dynamic_orchestrator.model.orchestrator.integration.dynamodb.OperacaoDynamoDB;
import com.kaikeventura.dynamic_orchestrator.model.orchestrator.integration.dynamodb.ParametrosChaveDynamoDB;
import com.kaikeventura.dynamic_orchestrator.model.orchestrator.step.PassoDynamoDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDbPassoExecutorTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    @Captor
    private ArgumentCaptor<PutItemRequest> putItemRequestCaptor;

    private DynamoDbPassoExecutor dynamoDbPassoExecutor;

    @BeforeEach
    void setUp() {
        dynamoDbPassoExecutor = new DynamoDbPassoExecutor(dynamoDbClient);
    }

    @Test
    void deveExecutarQueryComSucessoEAtualizarContexto() {
        // Given
        VariavelContexto contexto = new VariavelContexto();
        contexto.set("variavel-id-celular", "iphone-15-pro");

        PassoDynamoDB passo = criarPassoQuery();
        List<Variavel> variaveis = criarVariaveisMetadataQuery();

        Map<String, AttributeValue> mockItem = Map.of(
                "id_celular", AttributeValue.builder().s("iphone-15-pro").build(),
                "preco", AttributeValue.builder().n("9750").build()
        );
        QueryResponse mockResponse = QueryResponse.builder().items(mockItem).build();
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(mockResponse);

        // When
        dynamoDbPassoExecutor.executarPasso(passo, variaveis, contexto);

        // Then
        verify(dynamoDbClient).query(any(QueryRequest.class));
        assertEquals(9750L, contexto.get("variavel-preco-celular"));
    }

    @Test
    void deveExecutarPersistenciaComSucesso() {
        // Given
        VariavelContexto contexto = new VariavelContexto();
        contexto.set("variavel-id-transacao", "trans-123");
        contexto.set("variavel-id-celular", "iphone-15-pro");
        contexto.set("variavel-preco-celular", 9750L);
        contexto.set("variavel-id-cartao", "cartao-456");
        contexto.set("variavel-transacao-aprovada", true);

        PassoDynamoDB passo = criarPassoPersistencia();

        // When
        dynamoDbPassoExecutor.executarPasso(passo, List.of(), contexto);

        // Then
        verify(dynamoDbClient).putItem(putItemRequestCaptor.capture());
        PutItemRequest capturedRequest = putItemRequestCaptor.getValue();

        assertEquals("transacao_tbl", capturedRequest.tableName());
        Map<String, AttributeValue> item = capturedRequest.item();
        assertEquals("trans-123", item.get("id_transacao").s());
        assertEquals("iphone-15-pro", item.get("id_celular").s());
        assertEquals("9750", item.get("preco").n());
        assertEquals("cartao-456", item.get("id_cartao").s());
        assertTrue(item.get("aprovado").bool());
    }

    private PassoDynamoDB criarPassoQuery() {
        PassoDynamoDB passo = new PassoDynamoDB();
        IntegracaoDynamoDB integracao = new IntegracaoDynamoDB();
        integracao.setOperacao(OperacaoDynamoDB.QUERY);
        integracao.setNomeTabela("celulares_tbl");

        ParametrosChaveDynamoDB params = new ParametrosChaveDynamoDB();
        params.setPartitionKey(Map.of("id_celular", "{{variaveis$.variavel-id-celular}}"));
        integracao.setParametrosChave(params);

        passo.setIntegracao(integracao);
        return passo;
    }

    private PassoDynamoDB criarPassoPersistencia() {
        PassoDynamoDB passo = new PassoDynamoDB();
        IntegracaoDynamoDB integracao = new IntegracaoDynamoDB();
        integracao.setOperacao(OperacaoDynamoDB.PERSISTENCY);
        integracao.setNomeTabela("transacao_tbl");

        ParametrosChaveDynamoDB params = new ParametrosChaveDynamoDB();
        params.setPartitionKey(Map.of("id_transacao", "{{variaveis$.variavel-id-transacao}}"));
        params.setSortKey(Map.of("id_celular", "{{variaveis$.variavel-id-celular}}"));
        integracao.setParametrosChave(params);

        integracao.setCampos(List.of(
                Map.of("preco", "{{variaveis$.variavel-preco-celular}}"),
                Map.of("id_cartao", "{{variaveis$.variavel-id-cartao}}"),
                Map.of("aprovado", "{{variaveis$.variavel-transacao-aprovada}}")
        ));

        passo.setIntegracao(integracao);
        return passo;
    }

    private List<Variavel> criarVariaveisMetadataQuery() {
        Variavel variavel = new Variavel();
        variavel.setId("variavel-preco-celular");
        Origem origem = new Origem(
                "orquestrador",
                "{{dynamodb$.passo-consulta-preco-celular$.resultado$.preco}}"
        );
        variavel.setOrigem(origem);
        return List.of(variavel);
    }
}
