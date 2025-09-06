package com.kaikeventura.dynamic_orchestrator.controller;

import com.kaikeventura.dynamic_orchestrator.request.FluxoRequest;
import com.kaikeventura.dynamic_orchestrator.service.FluxoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fluxos")
@RequiredArgsConstructor
public class FluxoController {

    private final FluxoService fluxoService;

    @PostMapping("/executar")
    public ResponseEntity<Map<String, Object>> executar(@RequestBody FluxoRequest entrada) {
        return ResponseEntity.ok(fluxoService.executarFluxo(entrada.getFluxo(), entrada.getDadosEntrada()));
    }
}
