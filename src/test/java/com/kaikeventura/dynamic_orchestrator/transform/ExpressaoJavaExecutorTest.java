package com.kaikeventura.dynamic_orchestrator.transform;

import com.kaikeventura.dynamic_orchestrator.engine.VariavelContexto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpressaoJavaExecutorTest {

    @InjectMocks
    private ExpressaoJavaExecutor expressaoJavaExecutor;

    @Mock
    private TransformadorFactory factory;

    @Mock
    private Transformador transformador;

    private VariavelContexto contexto;

    @BeforeEach
    void setUp() {
        contexto = new VariavelContexto();
    }

    @Test
    void deveExecutarExpressaoComSucesso() {
        // Given
        String expressao = "{{java(soma(var1, 10))}}";
        String[] params = {"var1", "10"};
        when(factory.getTransformador("soma")).thenReturn(transformador);
        when(transformador.transformar(TransformadorRequest)).thenReturn(25);

        // When
        Object resultado = expressaoJavaExecutor.executar(expressao, contexto);

        // Then
        assertEquals(25, resultado);
        verify(factory).getTransformador("soma");
        verify(transformador).transformar(TransformadorRequest);
    }

    @Test
    void deveRetornarStringOriginalSeNaoForExpressaoJava() {
        // Given
        String expressao = "apenas uma string normal";

        // When
        Object resultado = expressaoJavaExecutor.executar(expressao, contexto);

        // Then
        assertEquals(expressao, resultado);
        verifyNoInteractions(factory);
    }

    @Test
    void deveLancarExcecaoSeTransformadorNaoForEncontrado() {
        // Given
        String expressao = "{{java(naoExiste(1, 2))}}";
        when(factory.getTransformador("naoExiste")).thenThrow(new IllegalArgumentException("Transformador não encontrado"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> expressaoJavaExecutor.executar(expressao, contexto));
    }

    @Test
    void deveLidarComEspacosEmBrancoNaExpressao() {
        // Given
        String expressao = "{{java(  soma  (  var1  ,  10  )  )}}";
        String[] params = {"var1", "10"};
        when(factory.getTransformador("soma")).thenReturn(transformador);
        when(transformador.transformar(TransformadorRequest)).thenReturn(25);

        // When
        Object resultado = expressaoJavaExecutor.executar(expressao, contexto);

        // Then
        assertEquals(25, resultado);
    }

    @Test
    void deveExecutarExpressaoSemParametros() {
        // Given
        String expressao = "{{java(agora())}}";
        String[] params = {""};
        when(factory.getTransformador("agora")).thenReturn(transformador);
        when(transformador.transformar(TransformadorRequest)).thenReturn("2023-10-27");

        // When
        Object resultado = expressaoJavaExecutor.executar(expressao, contexto);

        // Then
        assertEquals("2023-10-27", resultado);
        verify(factory).getTransformador("agora");
        verify(transformador).transformar(TransformadorRequest);
    }
}
