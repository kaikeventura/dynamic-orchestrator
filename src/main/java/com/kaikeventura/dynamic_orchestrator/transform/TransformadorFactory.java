package com.kaikeventura.dynamic_orchestrator.transform;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransformadorFactory {

    private final Map<String, Transformador> transformadores = new HashMap<>();

    public TransformadorFactory(List<Transformador> transformadoresList) {
        for (Transformador t : transformadoresList) {
            transformadores.put(t.getNome(), t);
        }
    }

    public Transformador getTransformador(String nome) {
        Transformador t = transformadores.get(nome);
        if (t == null) {
            throw new IllegalArgumentException("Transformador não encontrado: " + nome);
        }
        return t;
    }
}
