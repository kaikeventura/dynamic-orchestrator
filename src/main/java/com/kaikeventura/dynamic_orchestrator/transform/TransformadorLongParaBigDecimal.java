package com.kaikeventura.dynamic_orchestrator.transform;

import com.kaikeventura.dynamic_orchestrator.engine.VariavelContexto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class TransformadorLongParaBigDecimal implements Transformador {

    @Override
    public String getNome() {
        return "transformadorLongParaBigDecimal";
    }

    @Override
    public Object transformar(String[] parametros, VariavelContexto contexto) {
        if (parametros.length != 2) {
            throw new IllegalArgumentException("Parâmetros inválidos para transformadorLongParaBigDecimal");
        }

        String chaveVariavel = parametros[0].trim();
        int casasDecimais = Integer.parseInt(parametros[1].trim());

        if (chaveVariavel.startsWith("variaveis$.")) {
            chaveVariavel = chaveVariavel.replace("variaveis$.", "");
        }

        Object valorOriginal = contexto.get(chaveVariavel);
        if (valorOriginal instanceof Number) {
            long longValue = ((Number) valorOriginal).longValue();
            return new BigDecimal(BigInteger.valueOf(longValue), casasDecimais);
        }

        throw new IllegalArgumentException("Valor da variável não é numérico: " + chaveVariavel);
    }
}
