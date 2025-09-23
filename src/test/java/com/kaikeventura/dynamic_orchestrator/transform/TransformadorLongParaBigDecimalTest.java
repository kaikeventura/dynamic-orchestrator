package com.kaikeventura.dynamic_orchestrator.transform;

import com.kaikeventura.dynamic_orchestrator.engine.VariavelContexto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransformadorLongParaBigDecimalTest {

    private TransformadorLongParaBigDecimal transformador;
    private VariavelContexto contexto;

    @BeforeEach
    void setUp() {
        transformador = new TransformadorLongParaBigDecimal();
        contexto = new VariavelContexto();
    }

    @Test
    void deveRetornarNomeCorreto() {
        assertEquals("transformadorLongParaBigDecimal", transformador.getNome());
    }

    @Test
    void deveTransformarLongParaBigDecimalComSucesso() {
        // Given
        contexto.set("valorProduto", 12345L);
        var request = new TransformadorRequest(new String[]{"variaveis$.valorProduto", "2"}, null, contexto);

        // When
        Object resultado = transformador.transformar(request);

        // Then
        assertEquals(new BigDecimal("123.45"), resultado);
    }

    @Test
    void deveTransformarComTresCasasDecimais() {
        // Given
        contexto.set("valorProduto", 10000L);
        var request = new TransformadorRequest(new String[]{"valorProduto", "3"}, null, contexto);

        // When
        Object resultado = transformador.transformar(request);

        // Then
        assertEquals(new BigDecimal("10.000"), resultado);
    }

    @Test
    void deveTransformarUnidadeParaDecimal() {
        // Given
        contexto.set("valorProduto", 1L);
        var request = new TransformadorRequest(new String[]{"valorProduto", "2"}, null, contexto);

        // When
        Object resultado = transformador.transformar(request);

        // Then
        assertEquals(new BigDecimal("0.01"), resultado);
    }

    @Test
    void deveTransformarUnidadeParaMilhar() {
        // Given
        contexto.set("valorProduto", 1L);
        var request = new TransformadorRequest(new String[]{"valorProduto", "3"}, null, contexto);

        // When
        Object resultado = transformador.transformar(request);

        // Then
        assertEquals(new BigDecimal("0.001"), resultado);
    }

    @Test
    void deveLancarExcecaoParaNumeroInvalidoDeParametros() {
        // Given
        var request = new TransformadorRequest(new String[]{"variaveis$.valorProduto"}, null, contexto);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transformador.transformar(request));
    }

    @Test
    void deveLancarExcecaoQuandoVariavelNaoForNumerica() {
        // Given
        contexto.set("valorProduto", "nao-e-numero");
        var request = new TransformadorRequest(new String[]{"variaveis$.valorProduto", "2"}, null, contexto);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transformador.transformar(request));
    }

    @Test
    void deveLancarExcecaoQuandoVariavelNaoExistir() {
        // Given
        var request = new TransformadorRequest(new String[]{"variaveis$.variavelInexistente", "2"}, null, contexto);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transformador.transformar(request));
    }

    @Test
    void deveLidarComParametrosComEspacos() {
        // Given
        contexto.set("valorProduto", 12345L);
        var request = new TransformadorRequest(new String[]{"  variaveis$.valorProduto  ", "  2  "}, null, contexto);

        // When
        Object resultado = transformador.transformar(request);

        // Then
        assertEquals(new BigDecimal("123.45"), resultado);
    }

    @Test
    void deveFuncionarSemPrefixoVariaveis() {
        // Given
        contexto.set("valorProduto", 12345L);
        var request = new TransformadorRequest(new String[]{"valorProduto", "2"}, null, contexto);

        // When
        Object resultado = transformador.transformar(request);

        // Then
        assertEquals(new BigDecimal("123.45"), resultado);
    }
}
