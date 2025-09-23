package com.kaikeventura.dynamic_orchestrator.engine;

import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import com.kaikeventura.dynamic_orchestrator.transform.ExpressaoJavaExecutor;
import com.kaikeventura.dynamic_orchestrator.validador.ValidadorContrato;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FluxoEngine {

    private final PassoExecutorFactory executorFactory;
    private final ValidadorContrato validadorContrato;
    private final ExpressaoJavaExecutor expressaoJavaExecutor;

    public Map<String, Object> executar(FluxoConfig config, Map<String, Object> dadosEntrada) {
        VariavelContexto contexto = new VariavelContexto();

        validadorContrato.validar(config.getContrato(), dadosEntrada);
        inicializarVariaveis(config, dadosEntrada, contexto);

        List<FluxoConfig.Variavel> variaveis = config.getMetadados().getVariaveis();
        config.getOrquestrador().getPassos().stream()
                .sorted(Comparator.comparingInt(FluxoConfig.PassoBase::getOrdem))
                .forEach(passo -> executarPasso(passo, variaveis, contexto));

        return montarSaida(config, contexto);
    }

    private void inicializarVariaveis(FluxoConfig config, Map<String, Object> dadosEntrada, VariavelContexto contexto) {
        for (FluxoConfig.Variavel var : config.getMetadados().getVariaveis()) {
            if ("contrato".equals(var.getOrigem().getTipoReferencia())) {

                String idContrato = var.getOrigem().getIdReferencia()
                        .replace("{{entrada$.", "")
                        .replace("}}", "");

                String nomeCampo = config.getContrato().getEntrada().stream()
                        .filter(c -> c.getId().equals(idContrato))
                        .findFirst()
                        .map(FluxoConfig.CampoContrato::getNomeCampo)
                        .orElseThrow(() -> new IllegalArgumentException("Campo não encontrado no contrato: " + idContrato));

                Object valor = dadosEntrada.get(nomeCampo);

                contexto.set(var.getId(), valor);
            }
        }
    }

    private void executarPasso(FluxoConfig.PassoBase passo, List<FluxoConfig.Variavel> variaveis, VariavelContexto contexto) {
        try {
            PassoExecutor executor = executorFactory.getExecutor(passo.getTipo());
            executor.executarPasso(passo, variaveis, contexto);
        } catch (Exception e) {
            throw new RuntimeException("Erro no passo: " + passo.getId(), e);
        }
    }

    private Map<String, Object> montarSaida(FluxoConfig config, VariavelContexto contexto) {
        Map<String, Object> saida = new HashMap<>();
        for (FluxoConfig.CampoContratoSaida campoSaida : config.getContrato().getSaida()) {
            String valor = campoSaida.getValor();
            if (valor.contains("{{java")) {
                saida.put(campoSaida.getNomeCampo(), expressaoJavaExecutor.executar(valor, config, contexto));
            } else {
                String varId = valor.replace("{{variaveis$.", "").replace("}}", "");
                saida.put(campoSaida.getNomeCampo(), contexto.get(varId));
            }
        }
        return saida;
    }
}
