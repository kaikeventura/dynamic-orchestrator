package com.kaikeventura.dynamic_orchestrator.transform;

public interface Transformador {
    String getNome();
    Object transformar(TransformadorRequest request);
}
