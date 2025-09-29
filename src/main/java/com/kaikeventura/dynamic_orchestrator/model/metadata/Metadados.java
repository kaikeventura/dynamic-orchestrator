package com.kaikeventura.dynamic_orchestrator.model.metadata;

import lombok.Data;

import java.util.List;

@Data
public class Metadados {
    private List<Variavel> variaveis;
    private List<Mapeamento> mapeamentos;
}
