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
    public void executarPasso(FluxoConfig.Passo passo, List<FluxoConfig.Variavel> variaveis, VariavelContexto contexto) {
        FluxoConfig.Requisicao req = passo.getIntegracao().getRequisicao();

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
                    Object responseBody = objectMapper.readValue(response.getBody(), Object.class);
                    if (responseBody != null) {
                        salvarRespostaNoContexto(passo, responseBody, variaveis, contexto);
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
            FluxoConfig.Passo passo,
            Object responseBody,
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

                    if (responseBody instanceof LinkedHashMap<?,?>) {
                        var responseBodyLinkedHashMap = (LinkedHashMap) responseBody;

                        if (passo.getId().equals(idPassoReferencia) && responseBodyLinkedHashMap.containsKey(campoResposta)) {
                            contexto.set(var.getId(), responseBodyLinkedHashMap.get(campoResposta));
                        }
                    }

                    if (responseBody instanceof List<?>) {
                        var responseBodyList = (List) responseBody;
                        if (campoResposta.contains("<List>")) {
                            contexto.set(var.getId(), responseBodyList);
                        }
                    }
                }
            }
        }
    }
}
