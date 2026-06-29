package br.edu.iftm.edumetrics.controller;

import br.edu.iftm.edumetrics.service.AutocompletarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaController {
    private final AutocompletarService autocompletarService;

    public DisciplinaController(AutocompletarService autocompletarService) {
        this.autocompletarService = autocompletarService;
    }

    @GetMapping("/autocompletar")
    public ResponseEntity<List<String>> autocompletar(@RequestParam("q") String termo) {
        return ResponseEntity.ok(autocompletarService.autocompletar(termo, 10));
    }
}
