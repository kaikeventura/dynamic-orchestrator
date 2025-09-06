package com.kaikeventura.dynamic_orchestrator.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PassoExecutorFactory {
    private final List<PassoExecutor> executores;

    public PassoExecutor getExecutor(String tipo) {
        return executores.stream()
                .filter(e -> e.suporta(tipo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum executor para tipo: " + tipo));
    }
}
