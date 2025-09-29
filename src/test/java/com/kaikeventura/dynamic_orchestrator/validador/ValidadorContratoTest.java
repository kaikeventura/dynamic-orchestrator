package com.kaikeventura.dynamic_orchestrator.validador;

import com.kaikeventura.dynamic_orchestrator.model.contract.CampoContrato;
import com.kaikeventura.dynamic_orchestrator.model.contract.Contrato;
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
        Contrato contrato = new Contrato();
        CampoContrato campoObrigatorio = new CampoContrato("id1", "campo1", true);
        contrato.setEntrada(Collections.singletonList(campoObrigatorio));

        Map<String, Object> dadosEntrada = Collections.singletonMap("campo1", "valor");

        // When & Then
        assertDoesNotThrow(() -> validadorContrato.validar(contrato, dadosEntrada));
    }

    @Test
    void deveLancarExcecaoQuandoCampoObrigatorioEstaAusente() {
        // Given
        Contrato contrato = new Contrato();
        CampoContrato campoObrigatorio = new CampoContrato("id1", "campo1", true);
        contrato.setEntrada(Collections.singletonList(campoObrigatorio));

        Map<String, Object> dadosEntrada = Collections.emptyMap();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> validadorContrato.validar(contrato, dadosEntrada));
    }

    @Test
    void deveValidarComSucessoQuandoNaoHaCamposObrigatorios() {
        // Given
        Contrato contrato = new Contrato();
        CampoContrato campoOpcional = new CampoContrato("id1", "campo1", false);
        contrato.setEntrada(Collections.singletonList(campoOpcional));

        Map<String, Object> dadosEntrada = Collections.emptyMap();

        // When & Then
        assertDoesNotThrow(() -> validadorContrato.validar(contrato, dadosEntrada));
    }

    @Test
    void deveValidarComSucessoQuandoHaCamposExtrasNaEntrada() {
        // Given
        Contrato contrato = new Contrato();
        CampoContrato campoObrigatorio = new CampoContrato("id1", "campo1", true);
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
        Contrato contrato = new Contrato();
        CampoContrato campoObrigatorio = new CampoContrato("id1", "campo1", true);
        CampoContrato campoOpcional = new CampoContrato("id2", "campo2", false);
        contrato.setEntrada(Arrays.asList(campoObrigatorio, campoOpcional));

        Map<String, Object> dadosEntrada = Collections.singletonMap("campo1", "valor");

        // When & Then
        assertDoesNotThrow(() -> validadorContrato.validar(contrato, dadosEntrada));
    }
}
