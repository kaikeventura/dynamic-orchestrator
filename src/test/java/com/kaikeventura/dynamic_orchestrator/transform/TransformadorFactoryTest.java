package com.kaikeventura.dynamic_orchestrator.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransformadorFactoryTest {

    @Mock
    private Transformador transformadorSoma;

    @Mock
    private Transformador transformadorConcat;

    @Test
    void deveRetornarTransformadorCorreto() {
        // Given
        when(transformadorSoma.getNome()).thenReturn("soma");
        TransformadorFactory factory = new TransformadorFactory(Collections.singletonList(transformadorSoma));

        // When
        Transformador resultado = factory.getTransformador("soma");

        // Then
        assertEquals(transformadorSoma, resultado);
    }

    @Test
    void deveLancarExcecaoQuandoTransformadorNaoForEncontrado() {
        // Given
        TransformadorFactory factory = new TransformadorFactory(Collections.emptyList());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> factory.getTransformador("naoExiste"));
    }

    @Test
    void deveInicializarComMultiplosTransformadores() {
        // Given
        when(transformadorSoma.getNome()).thenReturn("soma");
        when(transformadorConcat.getNome()).thenReturn("concat");
        TransformadorFactory factory = new TransformadorFactory(Arrays.asList(transformadorSoma, transformadorConcat));

        // When
        Transformador resultadoSoma = factory.getTransformador("soma");
        Transformador resultadoConcat = factory.getTransformador("concat");

        // Then
        assertEquals(transformadorSoma, resultadoSoma);
        assertEquals(transformadorConcat, resultadoConcat);
    }

    @Test
    void deveSerSensivelACasoNoNomeDoTransformador() {
        // Given
        when(transformadorSoma.getNome()).thenReturn("soma");
        TransformadorFactory factory = new TransformadorFactory(Collections.singletonList(transformadorSoma));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> factory.getTransformador("Soma"));
    }
}
