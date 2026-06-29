package br.edu.iftm.edumetrics.service;

import br.edu.iftm.edumetrics.domain.Disciplina;
import br.edu.iftm.edumetrics.estruturas.Trie;
import br.edu.iftm.edumetrics.repository.DisciplinaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutocompletarService {
    private final Trie trie = new Trie();
    private final DisciplinaRepository disciplinaRepository;

    public AutocompletarService(DisciplinaRepository disciplinaRepository) {
        this.disciplinaRepository = disciplinaRepository;
    }

    @PostConstruct
    public void carregarDisciplinas() {
        disciplinaRepository.findAll().forEach(this::indexar);
    }

    public void indexar(Disciplina disciplina) {
        indexar(disciplina.getNome());
    }

    public void indexar(String termo) {
        trie.inserir(termo);
    }

    public List<String> autocompletar(String prefixo, int limite) {
        return trie.autocompletar(prefixo, limite);
    }

    public int totalIndexado() {
        return trie.size();
    }
}
