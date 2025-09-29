package com.kaikeventura.dynamic_orchestrator.transform;

import com.kaikeventura.dynamic_orchestrator.engine.VariavelContexto;
import com.kaikeventura.dynamic_orchestrator.model.FluxoConfig;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExpressaoJavaExecutor {

    private static final Pattern JAVA_PATTERN = Pattern.compile("\\{\\{java\\((.+)\\)}}");

    private final TransformadorFactory factory;

    public ExpressaoJavaExecutor(TransformadorFactory factory) {
        this.factory = factory;
    }

    public Object executar(String expressao, FluxoConfig config, VariavelContexto contexto) {
        Matcher matcher = JAVA_PATTERN.matcher(expressao);
        if (!matcher.matches()) {
            return expressao;
        }

        String conteudo = matcher.group(1).trim();
        String nomeTransformador = conteudo.substring(0, conteudo.indexOf("(")).trim();
        String paramsStr = conteudo.substring(conteudo.indexOf("(") + 1, conteudo.lastIndexOf(")")).trim();

        String[] parametros = paramsStr.split("\\s*,\\s*");

        Transformador transformador = factory.getTransformador(nomeTransformador);
        return transformador.transformar(new TransformadorRequest(parametros, config, contexto));
    }
}
