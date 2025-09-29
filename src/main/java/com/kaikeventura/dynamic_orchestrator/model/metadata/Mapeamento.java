package com.kaikeventura.dynamic_orchestrator.model.metadata;

import lombok.Data;

import java.util.List;

@Data
public class Mapeamento {
    private String id;
    private List<CamposMapeamento> campos;
}
