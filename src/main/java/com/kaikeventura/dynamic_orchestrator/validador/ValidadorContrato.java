package com.kaikeventura.dynamic_orchestrator.validador;

import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ValidadorContrato {
    public void validar(FluxoConfig.Contrato contrato, Map<String, Object> dadosEntrada) {
        for (FluxoConfig.CampoContrato campo : contrato.getEntrada()) {
            if (campo.isObrigatorio() && !dadosEntrada.containsKey(campo.getNomeCampo())) {
                throw new IllegalArgumentException("Campo obrigatório ausente: " + campo.getNomeCampo());
            }
        }
    }
}
