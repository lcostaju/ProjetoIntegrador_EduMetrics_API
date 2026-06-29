package br.edu.iftm.edumetrics.controller;

import br.edu.iftm.edumetrics.domain.dto.RelatorioRequest;
import br.edu.iftm.edumetrics.messaging.RelatorioProducer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
    private final RelatorioProducer relatorioProducer;

    public RelatorioController(RelatorioProducer relatorioProducer) {
        this.relatorioProducer = relatorioProducer;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> solicitar(@Valid @RequestBody RelatorioRequest request) {
        String correlationId = relatorioProducer.solicitar(request);
        return ResponseEntity.accepted().body(Map.of(
            "correlationId", correlationId,
            "mensagem", "Relatorio em processamento"
        ));
    }
}
