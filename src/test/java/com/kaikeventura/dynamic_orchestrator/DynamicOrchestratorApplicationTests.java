package com.kaikeventura.dynamic_orchestrator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DynamicOrchestratorApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveExecutarFluxoProdutosEletronicos() throws Exception {
        String requestBody = """
            {
                "fluxo": "produtos-eletronicos",
                "dadosEntrada": {
                    "produtoId": "12345",
                    "produtoQuantidade": "10"
                }
            }
            """;

        mockMvc.perform(post("/fluxos/executar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorProduto").exists())
                .andExpect(jsonPath("$.valorProduto").isNotEmpty());
    }

    @Test
    void deveExecutarFluxoVendaCelulares() throws Exception {
        String requestBody = """
            {
                "fluxo": "venda-celulares",
                "dadosEntrada": {
                    "celularId": "123",
                    "cartaoId": "321"
                }
            }
            """;

        mockMvc.perform(post("/fluxos/executar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTransacao").exists())
                .andExpect(jsonPath("$.transacaoAprovada").exists());
    }
}
