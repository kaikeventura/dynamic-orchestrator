package com.kaikeventura.dynamic_orchestrator.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApiPassoExecutor implements PassoExecutor {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean suporta(String tipo) {
        return "API".equalsIgnoreCase(tipo);
    }

    @Override
    public void executarPasso(FluxoConfig.PassoBase passo, List<FluxoConfig.Variavel> variaveis, VariavelContexto contexto) {
        var passoAPI = (FluxoConfig.PassoAPI) passo;
        FluxoConfig.Requisicao req = passoAPI.getIntegracao().getRequisicao();

        Map<String, Object> headersMap = VariavelSubstituidor.substituir(req.getHeaders(), contexto);
        Map<String, Object> queryParams = VariavelSubstituidor.substituir(req.getQueryParams(), contexto);

        HttpHeaders headers = new HttpHeaders();
        headersMap.forEach((k, v) -> headers.add(k, String.valueOf(v)));

        String url = req.getUrl();
        if (!queryParams.isEmpty()) {
            String query = queryParams.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .reduce((a, b) -> a + "&" + b)
                    .orElse("");
            url += "?" + query;
        }

        url = VariavelSubstituidor.substituir(url, contexto);

        HttpMethod httpMethod = HttpMethod.valueOf(req.getMetodo().toUpperCase());

        HttpEntity<Object> requestEntity;
        if (HttpMethod.GET.equals(httpMethod) || HttpMethod.DELETE.equals(httpMethod)) {
            requestEntity = new HttpEntity<>(headers);
        } else {
            Object body = VariavelSubstituidor.substituir(req.getBody(), contexto);
            requestEntity = new HttpEntity<>(body, headers);
        }

        ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, requestEntity, String.class);

        if (response.getBody() != null) {
            MediaType contentType = response.getHeaders().getContentType();
            try {
                if (contentType != null) {
                    LinkedHashMap responseBody = objectMapper.readValue(response.getBody(), LinkedHashMap.class);
                    if (responseBody != null) {
                        salvarRespostaNoContexto(passoAPI, responseBody, variaveis, contexto);
                    }
                } else {
                    contexto.set(passo.getId(), response.getBody());
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro ao processar resposta do passo: " + passo.getId(), e);
            }
        }
    }

    private void salvarRespostaNoContexto(
            FluxoConfig.PassoAPI passo,
            LinkedHashMap<Object, Object> responseBody,
            List<FluxoConfig.Variavel> variaveis,
            VariavelContexto contexto
    ) {
        // percorre todas as variáveis do fluxo
        for (FluxoConfig.Variavel var : variaveis) {

            // só considera variáveis que vêm do orquestrador
            if ("orquestrador".equals(var.getOrigem().getTipoReferencia())) {

                String idReferencia = var.getOrigem().getIdReferencia();
                // exemplo: {{api$.passo-consulta-preco-produto$.resposta$.preco}}

                // extrai o passo e o campo
                String pattern = "\\{\\{api\\$\\.(.+?)\\$\\.resposta\\$\\.(.+?)}}";
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(idReferencia);

                if (matcher.matches()) {
                    String idPassoReferencia = matcher.group(1);  // ex: passo-consulta-preco-produto
                    String campoResposta = matcher.group(2);      // ex: preco

                    // se for o passo atual e a resposta contém o campo
                    if (passo.getId().equals(idPassoReferencia) && responseBody.containsKey(campoResposta)) {
                        contexto.set(var.getId(), responseBody.get(campoResposta));
                    }
                }
            }
        }
    }
}
