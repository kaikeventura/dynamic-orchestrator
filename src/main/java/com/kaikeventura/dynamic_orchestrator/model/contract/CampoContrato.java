package com.kaikeventura.dynamic_orchestrator.model.contract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampoContrato {
    private String id;
    private String nomeCampo;
    private boolean obrigatorio;
}
