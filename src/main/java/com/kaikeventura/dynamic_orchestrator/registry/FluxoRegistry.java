package com.kaikeventura.dynamic_orchestrator.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FluxoRegistry {

    private final Map<String, FluxoConfig> fluxos = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void carregarFluxos() throws IOException {
        var resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:fluxos/*.json");

        for (Resource resource : resources) {
            FluxoConfig config = objectMapper.readValue(resource.getInputStream(), FluxoConfig.class);
            fluxos.put(config.getFluxo(), config);
        }
    }

    public FluxoConfig getFluxo(String id) {
        return fluxos.get(id);
    }
}
