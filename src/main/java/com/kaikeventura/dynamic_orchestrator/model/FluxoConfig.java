package com.kaikeventura.dynamic_orchestrator.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
public class FluxoConfig {
    private String fluxo;
    private Contrato contrato;
    private Metadados metadados;
    private Orquestrador orquestrador;

    @Data
    public static class Contrato {
        private List<CampoContrato> entrada;
        private List<CampoContratoSaida> saida;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CampoContrato {
        private String id;
        private String nomeCampo;
        private boolean obrigatorio;
    }

    @Data
    public static class CampoContratoSaida {
        private String id;
        private String nomeCampo;
        private String valor;
    }

    @Data
    public static class Metadados {
        private List<Variavel> variaveis;
    }

    @Data
    public static class Variavel {
        private String id;
        private Origem origem;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Origem {
        private String tipoReferencia;
        private String idReferencia;
    }

    @Data
    public static class Orquestrador {
        private List<PassoBase> passos;
    }

    @Data
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "tipo",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PassoAPI.class, name = "API"),
            @JsonSubTypes.Type(value = PassoDynamoDB.class, name = "DYNAMODB")
    })
    public static abstract class PassoBase {
        private String id;
        private String tipo;
        private int ordem;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PassoAPI extends PassoBase {
        private IntegracaoAPI integracao;
    }

    @Data
    public static class IntegracaoAPI {
        private Requisicao requisicao;
        private Resposta resposta;
    }

    @Data
    public static class Requisicao {
        private String url;
        private String metodo;
        private Map<String, Object> headers;
        private Map<String, Object> queryParams;
        private Map<String, Object> body;
    }

    @Data
    public static class Resposta {
        private Object body;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PassoDynamoDB extends PassoBase {
        private IntegracaoDynamoDB integracao;
    }

    @Data
    public static class IntegracaoDynamoDB {
        private String nomeTabela;
        private OperacaoDynamoDB operacao;
        private ParametrosChaveDynamoDB parametrosChave;
        private List<Map<String, Object>> campos;
        private Object resultado;
    }

    public enum OperacaoDynamoDB {
        QUERY, PERSISTENCY
    }

    @Data
    public static class ParametrosChaveDynamoDB {
        private Map<String, Object> partitionKey;
        private Map<String, Object> sortKey;
    }
}
