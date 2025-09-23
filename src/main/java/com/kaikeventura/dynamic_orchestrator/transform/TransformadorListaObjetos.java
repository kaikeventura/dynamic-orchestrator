package com.kaikeventura.dynamic_orchestrator.transform;

import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransformadorListaObjetos implements Transformador {

    @Override
    public String getNome() {
        return "transformadorListaObjetos";
    }

    @Override
    public Object transformar(TransformadorRequest request) {
        var parametros = request.parametros();
        var contexto = request.contexto();
        var config = request.config();

        if (parametros.length != 2) {
            throw new IllegalArgumentException("O transformadorListaObjetos espera exatamente 2 parâmetros: a variável da lista e o id do mapeamento.");
        }

        // 1. Extrair IDs dos parâmetros
        String variavelId = extrairId(parametros[0], "variaveis");
        String mapeamentoId = extrairId(parametros[1], "mapeamentos");

        // 2. Obter a lista e o mapeamento
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> listaOriginal = (List<Map<String, Object>>) contexto.get(variavelId);
        if (listaOriginal == null) {
            throw new IllegalArgumentException("A variável da lista '" + variavelId + "' não foi encontrada no contexto.");
        }

        FluxoConfig.Mapeamento mapeamento = config.getMetadados().getMapeamentos().stream()
                .filter(m -> m.getId().equals(mapeamentoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("O mapeamento '" + mapeamentoId + "' não foi encontrado nos metadados."));

        // 3. Transformar a lista
        List<Map<String, Object>> listaTransformada = new ArrayList<>();
        for (Map<String, Object> objetoOriginal : listaOriginal) {
            Map<String, Object> objetoTransformado = new HashMap<>();
            for (FluxoConfig.CamposMapeamento campoMapeamento : mapeamento.getCampos()) {
                String chaveOrigem = campoMapeamento.getOrigem();
                String chaveDestino = campoMapeamento.getDestino();

                if (objetoOriginal.containsKey(chaveOrigem)) {
                    objetoTransformado.put(chaveDestino, objetoOriginal.get(chaveOrigem));
                }
            }
            listaTransformada.add(objetoTransformado);
        }

        return listaTransformada;
    }

    private String extrairId(String parametro, String tipo) {
        parametro = parametro.trim();
        String prefixo = String.format("{{%s$.", tipo);
        if (parametro.startsWith(prefixo) && parametro.endsWith("}}")) {
            return parametro.substring(prefixo.length(), parametro.length() - 2);
        }
        throw new IllegalArgumentException("Formato de parâmetro inválido: " + parametro);
    }
}
