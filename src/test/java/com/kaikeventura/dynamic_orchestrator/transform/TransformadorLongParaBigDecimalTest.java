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
        String[] params = {"variaveis$.valorProduto", "2"};

        // When
        Object resultado = transformador.transformar(params, contexto);

        // Then
        assertEquals(new BigDecimal("123.45"), resultado);
    }

    @Test
    void deveTransformarComTresCasasDecimais() {
        // Given
        contexto.set("valorProduto", 10000L);
        String[] params = {"valorProduto", "3"};

        // When
        Object resultado = transformador.transformar(params, contexto);

        // Then
        assertEquals(new BigDecimal("10.000"), resultado);
    }

    @Test
    void deveTransformarUnidadeParaDecimal() {
        // Given
        contexto.set("valorProduto", 1L);
        String[] params = {"valorProduto", "2"};

        // When
        Object resultado = transformador.transformar(params, contexto);

        // Then
        assertEquals(new BigDecimal("0.01"), resultado);
    }

    @Test
    void deveTransformarUnidadeParaMilhar() {
        // Given
        contexto.set("valorProduto", 1L);
        String[] params = {"valorProduto", "3"};

        // When
        Object resultado = transformador.transformar(params, contexto);

        // Then
        assertEquals(new BigDecimal("0.001"), resultado);
    }

    @Test
    void deveLancarExcecaoParaNumeroInvalidoDeParametros() {
        // Given
        String[] params = {"variaveis$.valorProduto"};

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transformador.transformar(params, contexto));
    }

    @Test
    void deveLancarExcecaoQuandoVariavelNaoForNumerica() {
        // Given
        contexto.set("valorProduto", "nao-e-numero");
        String[] params = {"variaveis$.valorProduto", "2"};

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transformador.transformar(params, contexto));
    }

    @Test
    void deveLancarExcecaoQuandoVariavelNaoExistir() {
        // Given
        String[] params = {"variaveis$.variavelInexistente", "2"};

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transformador.transformar(params, contexto));
    }

    @Test
    void deveLidarComParametrosComEspacos() {
        // Given
        contexto.set("valorProduto", 12345L);
        String[] params = {"  variaveis$.valorProduto  ", "  2  "};

        // When
        Object resultado = transformador.transformar(params, contexto);

        // Then
        assertEquals(new BigDecimal("123.45"), resultado);
    }

    @Test
    void deveFuncionarSemPrefixoVariaveis() {
        // Given
        contexto.set("valorProduto", 12345L);
        String[] params = {"valorProduto", "2"};

        // When
        Object resultado = transformador.transformar(params, contexto);

        // Then
        assertEquals(new BigDecimal("123.45"), resultado);
    }
}
