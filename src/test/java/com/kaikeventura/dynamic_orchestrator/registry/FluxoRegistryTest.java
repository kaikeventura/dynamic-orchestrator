package com.kaikeventura.dynamic_orchestrator.registry;

import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FluxoRegistryTest {

    private FluxoRegistry fluxoRegistry;

    @BeforeEach
    void setUp() {
        fluxoRegistry = new FluxoRegistry();
    }

    @Test
    void deveCarregarFluxosDoClasspath() throws IOException {
        // When
        fluxoRegistry.carregarFluxos();
        FluxoConfig resultado = fluxoRegistry.getFluxo("test-fluxo");

        // Then
        assertNotNull(resultado);
        assertEquals("test-fluxo", resultado.getFluxo());
    }

    @Test
    void deveRetornarNuloParaFluxoInexistente() throws IOException {
        // When
        fluxoRegistry.carregarFluxos();
        FluxoConfig resultado = fluxoRegistry.getFluxo("fluxo-inexistente");

        // Then
        assertNull(resultado);
    }
}
