package com.kaikeventura.dynamic_orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
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
        private List<Estrutura> estruturas;
        private List<Mapeamento> mapeamentos;
    }

    @Data
    public static class Variavel {
        private String id;
        private Origem origem;
    }

    @Data
    public static class Estrutura {
        private String id;
        private Object valor;
    }

    @Data
    public static class Mapeamento {
        private String id;
        private Object valor;
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
        private List<Passo> passos;
    }

    @Data
    public static class Passo {
        private String id;
        private String tipo;
        private int ordem;
        private Integracao integracao;
    }

    @Data
    public static class Integracao {
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
}
