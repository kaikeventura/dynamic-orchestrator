package com.kaikeventura.dynamic_orchestrator.engine;

import java.util.HashMap;
import java.util.Map;

public class VariavelSubstituidor {
    private VariavelSubstituidor() {}

    public static Map<String, Object> substituir(Map<String, Object> original, VariavelContexto contexto) {
        if (original == null) return Map.of();
        Map<String, Object> resultado = new HashMap<>();
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            String valor = String.valueOf(entry.getValue());
            if (valor.contains("{{variaveis$.")) {
                String varId = valor.replace("{{variaveis$.", "").replace("}}", "");
                resultado.put(entry.getKey(), contexto.get(varId));
            } else {
                resultado.put(entry.getKey(), valor);
            }
        }
        return resultado;
    }
}
