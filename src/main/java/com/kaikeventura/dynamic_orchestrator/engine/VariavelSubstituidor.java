package com.kaikeventura.dynamic_orchestrator.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String substituir(String original, VariavelContexto contexto) {
        if (original == null || !original.contains("{{variaveis$.")) return original;

        var variaveisBase = extrairVariaveis(original);

        var valorTranformado = original;

        for (var variavelBase : variaveisBase) {
            var variavelChave = variavelBase.replace("{{", "").replace("}}", "");
            var variavelNome = variavelChave.replace("variaveis$.", "");
            valorTranformado = valorTranformado.replace(variavelBase, contexto.get(variavelNome).toString());
        }

        return valorTranformado;
    }

    private static List<String> extrairVariaveis(String texto) {
        if (texto == null) {
            return List.of();
        }
        List<String> variaveis = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\{(.+?)}}");
        Matcher matcher = pattern.matcher(texto);
        while (matcher.find()) {
            variaveis.add(matcher.group(0));
        }
        return variaveis;
    }
}
