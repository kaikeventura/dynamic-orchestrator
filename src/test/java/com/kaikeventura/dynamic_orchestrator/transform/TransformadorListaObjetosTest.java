package com.kaikeventura.dynamic_orchestrator.transform;

import com.kaikeventura.dynamic_orchestrator.engine.VariavelContexto;
import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import com.kaikeventura.dynamic_orchestrator.model.metadata.CamposMapeamento;
import com.kaikeventura.dynamic_orchestrator.model.metadata.Mapeamento;
import com.kaikeventura.dynamic_orchestrator.model.metadata.Metadados;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransformadorListaObjetosTest {

    private TransformadorListaObjetos transformador;
    private VariavelContexto contexto;
    private FluxoConfig fluxoConfig;

    @BeforeEach
    void setUp() {
        transformador = new TransformadorListaObjetos();
        contexto = new VariavelContexto();
        fluxoConfig = new FluxoConfig();
        fluxoConfig.setMetadados(new Metadados());
    }

    @Test
    void deveTransformarListaDeObjetosComSucesso() {
        // Given
        List<Map<String, Object>> listaOriginal = List.of(
                Map.of("paridade_compra", 1.0, "paridade_venda", 1.0, "campo_extra", "ignorar"),
                Map.of("paridade_compra", 0.9, "paridade_venda", 1.1)
        );
        contexto.set("variavel-lista-cotacoes", listaOriginal);

        Mapeamento mapeamento = criarMapeamentoCotacoes();
        fluxoConfig.getMetadados().setMapeamentos(List.of(mapeamento));

        var request = new TransformadorRequest(
                new String[]{"{{variaveis$.variavel-lista-cotacoes}}", "{{mapeamentos$.mapeamento-cotacoes}}"},
                fluxoConfig,
                contexto
        );

        // When
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultado = (List<Map<String, Object>>) transformador.transformar(request);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        // Verifica o primeiro objeto transformado
        Map<String, Object> primeiroObjeto = resultado.get(0);
        assertEquals(1.0, primeiroObjeto.get("paridadeCompra"));
        assertEquals(1.0, primeiroObjeto.get("paridadeVenda"));
        assertFalse(primeiroObjeto.containsKey("paridade_compra")); // Garante que a chave original foi removida
        assertFalse(primeiroObjeto.containsKey("campo_extra")); // Garante que campos não mapeados são ignorados

        // Verifica o segundo objeto transformado
        Map<String, Object> segundoObjeto = resultado.get(1);
        assertEquals(0.9, segundoObjeto.get("paridadeCompra"));
        assertEquals(1.1, segundoObjeto.get("paridadeVenda"));
    }

    @Test
    void deveLancarExcecaoSeNumeroDeParametrosForInvalido() {
        // Given
        var request = new TransformadorRequest(new String[]{"param1"}, fluxoConfig, contexto);

        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> transformador.transformar(request));
        assertEquals("O transformadorListaObjetos espera exatamente 2 parâmetros: a variável da lista e o id do mapeamento.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoSeVariavelDaListaNaoExistir() {
        // Given
        fluxoConfig.getMetadados().setMapeamentos(List.of(criarMapeamentoCotacoes()));
        var request = new TransformadorRequest(
                new String[]{"{{variaveis$.variavel-inexistente}}", "{{mapeamentos$.mapeamento-cotacoes}}"},
                fluxoConfig,
                contexto
        );

        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> transformador.transformar(request));
        assertEquals("A variável da lista 'variavel-inexistente' não foi encontrada no contexto.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoSeMapeamentoNaoExistir() {
        // Given
        contexto.set("variavel-lista-cotacoes", List.of());
        // A CORREÇÃO ESTÁ AQUI: Garantimos que a lista de mapeamentos não é nula.
        fluxoConfig.getMetadados().setMapeamentos(Collections.emptyList());

        var request = new TransformadorRequest(
                new String[]{"{{variaveis$.variavel-lista-cotacoes}}", "{{mapeamentos$.mapeamento-inexistente}}"},
                fluxoConfig,
                contexto
        );

        // When & Then
        var exception = assertThrows(IllegalArgumentException.class, () -> transformador.transformar(request));
        assertEquals("O mapeamento 'mapeamento-inexistente' não foi encontrado nos metadados.", exception.getMessage());
    }

    @Test
    void deveRetornarListaVaziaSeListaOriginalForVazia() {
        // Given
        contexto.set("variavel-lista-cotacoes", List.of());
        fluxoConfig.getMetadados().setMapeamentos(List.of(criarMapeamentoCotacoes()));
        var request = new TransformadorRequest(
                new String[]{"{{variaveis$.variavel-lista-cotacoes}}", "{{mapeamentos$.mapeamento-cotacoes}}"},
                fluxoConfig,
                contexto
        );

        // When
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultado = (List<Map<String, Object>>) transformador.transformar(request);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    private Mapeamento criarMapeamentoCotacoes() {
        Mapeamento mapeamento = new Mapeamento();
        mapeamento.setId("mapeamento-cotacoes");

        CamposMapeamento campo1 = new CamposMapeamento();
        campo1.setOrigem("paridade_compra");
        campo1.setDestino("paridadeCompra");

        CamposMapeamento campo2 = new CamposMapeamento();
        campo2.setOrigem("paridade_venda");
        campo2.setDestino("paridadeVenda");

        mapeamento.setCampos(List.of(campo1, campo2));
        return mapeamento;
    }
}
