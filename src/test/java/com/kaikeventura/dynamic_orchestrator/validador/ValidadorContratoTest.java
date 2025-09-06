package com.kaikeventura.dynamic_orchestrator.validador;

import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorContratoTest {

    private ValidadorContrato validadorContrato;

    @BeforeEach
    void setUp() {
        validadorContrato = new ValidadorContrato();
    }

    @Test
    void deveValidarComSucessoQuandoCamposObrigatoriosEstaoPresentes() {
        // Given
        FluxoConfig.Contrato contrato = new FluxoConfig.Contrato();
        FluxoConfig.CampoContrato campoObrigatorio = new FluxoConfig.CampoContrato("id1", "campo1", true);
        contrato.setEntrada(Collections.singletonList(campoObrigatorio));

        Map<String, Object> dadosEntrada = Collections.singletonMap("campo1", "valor");

        // When & Then
        assertDoesNotThrow(() -> validadorContrato.validar(contrato, dadosEntrada));
    }

    @Test
    void deveLancarExcecaoQuandoCampoObrigatorioEstaAusente() {
        // Given
        FluxoConfig.Contrato contrato = new FluxoConfig.Contrato();
        FluxoConfig.CampoContrato campoObrigatorio = new FluxoConfig.CampoContrato("id1", "campo1", true);
        contrato.setEntrada(Collections.singletonList(campoObrigatorio));

        Map<String, Object> dadosEntrada = Collections.emptyMap();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validadorContrato.validar(contrato, dadosEntrada));
    }

    @Test
    void deveValidarComSucessoQuandoNaoHaCamposObrigatorios() {
        // Given
        FluxoConfig.Contrato contrato = new FluxoConfig.Contrato();
        FluxoConfig.CampoContrato campoOpcional = new FluxoConfig.CampoContrato("id1", "campo1", false);
        contrato.setEntrada(Collections.singletonList(campoOpcional));

        Map<String, Object> dadosEntrada = Collections.emptyMap();

        // When & Then
        assertDoesNotThrow(() -> validadorContrato.validar(contrato, dadosEntrada));
    }

    @Test
    void deveValidarComSucessoQuandoHaCamposExtrasNaEntrada() {
        // Given
        FluxoConfig.Contrato contrato = new FluxoConfig.Contrato();
        FluxoConfig.CampoContrato campoObrigatorio = new FluxoConfig.CampoContrato("id1", "campo1", true);
        contrato.setEntrada(Collections.singletonList(campoObrigatorio));

        Map<String, Object> dadosEntrada = new HashMap<>();
        dadosEntrada.put("campo1", "valor");
        dadosEntrada.put("campoExtra", "valorExtra");

        // When & Then
        assertDoesNotThrow(() -> validadorContrato.validar(contrato, dadosEntrada));
    }

    @Test
    void deveValidarComSucessoQuandoCampoOpcionalEstaAusente() {
        // Given
        FluxoConfig.Contrato contrato = new FluxoConfig.Contrato();
        FluxoConfig.CampoContrato campoObrigatorio = new FluxoConfig.CampoContrato("id1", "campo1", true);
        FluxoConfig.CampoContrato campoOpcional = new FluxoConfig.CampoContrato("id2", "campo2", false);
        contrato.setEntrada(Arrays.asList(campoObrigatorio, campoOpcional));

        Map<String, Object> dadosEntrada = Collections.singletonMap("campo1", "valor");

        // When & Then
        assertDoesNotThrow(() -> validadorContrato.validar(contrato, dadosEntrada));
    }
}
