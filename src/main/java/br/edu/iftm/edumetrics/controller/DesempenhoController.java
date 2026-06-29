package br.edu.iftm.edumetrics.controller;

import br.edu.iftm.edumetrics.domain.dto.DesempenhoDTO;
import br.edu.iftm.edumetrics.domain.dto.DesempenhoRequest;
import br.edu.iftm.edumetrics.domain.dto.RankingItemDTO;
import br.edu.iftm.edumetrics.service.DesempenhoService;
import br.edu.iftm.edumetrics.service.RankingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class DesempenhoController {
    private final DesempenhoService desempenhoService;
    private final RankingService rankingService;

    public DesempenhoController(DesempenhoService desempenhoService,
                                RankingService rankingService) {
        this.desempenhoService = desempenhoService;
        this.rankingService = rankingService;
    }

    @PostMapping("/api/desempenhos")
    public ResponseEntity<DesempenhoDTO> registrar(@Valid @RequestBody DesempenhoRequest request) {
        DesempenhoDTO salvo = desempenhoService.registrar(request);
        return ResponseEntity.created(URI.create("/api/alunos/" + request.alunoId() + "/desempenho"))
            .body(salvo);
    }

    @GetMapping("/api/alunos/{id}/desempenho")
    public ResponseEntity<List<DesempenhoDTO>> listarPorAluno(@PathVariable Long id) {
        return ResponseEntity.ok(desempenhoService.listarPorAluno(id));
    }

    @GetMapping("/api/ranking")
    public ResponseEntity<List<RankingItemDTO>> ranking(@RequestParam(defaultValue = "10") int top) {
        return ResponseEntity.ok(rankingService.topK(top));
    }
}
