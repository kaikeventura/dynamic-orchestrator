package com.kaikeventura.dynamic_orchestrator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaikeventura.dynamic_orchestrator.request.FluxoRequest;
import com.kaikeventura.dynamic_orchestrator.service.FluxoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FluxoController.class)
class FluxoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FluxoService fluxoService;

    @Test
    void deveExecutarFluxoComSucesso() throws Exception {
        // Given
        FluxoRequest request = new FluxoRequest("meu-fluxo", Collections.singletonMap("chave", "valor"));
        Map<String, Object> serviceResponse = Collections.singletonMap("resultado", "sucesso");

        when(fluxoService.executarFluxo(any(), any())).thenReturn(serviceResponse);

        // When & Then
        mockMvc.perform(post("/fluxos/executar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("sucesso"));
    }
}
