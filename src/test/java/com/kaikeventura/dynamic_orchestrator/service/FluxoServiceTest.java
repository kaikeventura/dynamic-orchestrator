package com.kaikeventura.dynamic_orchestrator.service;

import com.kaikeventura.dynamic_orchestrator.engine.FluxoEngine;
import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import com.kaikeventura.dynamic_orchestrator.registry.FluxoRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FluxoServiceTest {

    @InjectMocks
    private FluxoService fluxoService;

    @Mock
    private FluxoRegistry registry;

    @Mock
    private FluxoEngine engine;

    @Test
    void deveExecutarFluxoComSucesso() {
        // Given
        String nomeFluxo = "meu-fluxo";
        Map<String, Object> dadosEntrada = Collections.singletonMap("chave", "valor");
        FluxoConfig fluxoConfig = new FluxoConfig();
        Map<String, Object> resultadoEsperado = Collections.singletonMap("resultado", "sucesso");

        when(registry.getFluxo(nomeFluxo)).thenReturn(fluxoConfig);
        when(engine.executar(fluxoConfig, dadosEntrada)).thenReturn(resultadoEsperado);

        // When
        Map<String, Object> resultadoReal = fluxoService.executarFluxo(nomeFluxo, dadosEntrada);

        // Then
        assertEquals(resultadoEsperado, resultadoReal);
        verify(registry).getFluxo(nomeFluxo);
        verify(engine).executar(fluxoConfig, dadosEntrada);
    }

    @Test
    void deveLancarExcecaoQuandoFluxoNaoForEncontrado() {
        // Given
        String nomeFluxo = "fluxo-inexistente";
        Map<String, Object> dadosEntrada = Collections.emptyMap();

        when(registry.getFluxo(nomeFluxo)).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> fluxoService.executarFluxo(nomeFluxo, dadosEntrada));
        verify(engine, never()).executar(any(), any());
    }
}
