package com.kaikeventura.dynamic_orchestrator.transform;

import com.kaikeventura.dynamic_orchestrator.engine.VariavelContexto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TrasformadorListaDeObjetos implements Transformador {

    @Override
    public String getNome() {
        return "trasformadorListaDeObjetos";
    }

    @Override
    public Object transformar(String[] parametros, VariavelContexto contexto) {

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> listaRaw = (List<Map<String, Object>>) args.get(0);

        @SuppressWarnings("unchecked")
        Map<String, String> estruturaRaw = (Map<String, String>) args.get(1);

        @SuppressWarnings("unchecked")
        Map<String, String> estruturaDestino = (Map<String, String>) args.get(2);

        @SuppressWarnings("unchecked")
        List<Map<String, String>> mapeamentos = (List<Map<String, String>>) args.get(3);

        List<Map<String, Object>> resultado = new ArrayList<>();

        return null;
    }
}
