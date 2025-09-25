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

    @Test
    void deveExecutarFluxoCotacoesMoedas() throws Exception {
        String requestBody = """
            {
                "fluxo": "cotacoes-moedas",
                "dadosEntrada": {
                    "nomeMoeda": "USD",
                    "dataReferenciaCotacao": "2025-09-18"
                }
            }
            """;

        mockMvc.perform(post("/fluxos/executar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cotacoes").exists())
                .andExpect(jsonPath("$.cotacoes").isNotEmpty())
                .andExpect(jsonPath("$.cotacoes[0].cotacaoCompra").exists())
                .andExpect(jsonPath("$.cotacoes[0].cotacaoCompra").isNumber())
                .andExpect(jsonPath("$.cotacoes[0].cotacaoVenda").exists())
                .andExpect(jsonPath("$.cotacoes[0].cotacaoVenda").isNumber())
                .andExpect(jsonPath("$.cotacoes[0].tipoBoletim").exists())
                .andExpect(jsonPath("$.cotacoes[0].tipoBoletim").isString())
                .andExpect(jsonPath("$.cotacoes[0].dataHoraCotacao").exists())
                .andExpect(jsonPath("$.cotacoes[0].dataHoraCotacao").isNotEmpty())
                .andExpect(jsonPath("$.cotacoes[0].paridadeCompra").exists())
                .andExpect(jsonPath("$.cotacoes[0].paridadeCompra").isNumber())
                .andExpect(jsonPath("$.cotacoes[0].paridadeVenda").exists())
                .andExpect(jsonPath("$.cotacoes[0].paridadeVenda").isNumber())


        ;
    }
}
