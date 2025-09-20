package com.kaikeventura.dynamic_orchestrator.engine;

import java.util.HashMap;
import java.util.Map;

public class VariavelContexto {
    private final Map<String, Object> valores = new HashMap<>();

    public void set(String chave, Object valor) {
        valores.put(chave, valor);
    }

    public Object get(String chave) {
        return valores.get(chave);
    }
}
