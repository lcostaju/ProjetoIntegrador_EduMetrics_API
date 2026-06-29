package br.edu.iftm.edumetrics.service;

import br.edu.iftm.edumetrics.domain.Aluno;
import br.edu.iftm.edumetrics.domain.Desempenho;
import br.edu.iftm.edumetrics.domain.dto.RankingItemDTO;
import br.edu.iftm.edumetrics.repository.DesempenhoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Service
public class RankingService {
    private final DesempenhoRepository desempenhoRepository;

    public RankingService(DesempenhoRepository desempenhoRepository) {
        this.desempenhoRepository = desempenhoRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "ranking", key = "#top")
    public List<RankingItemDTO> topK(int top) {
        if (top <= 0) {
            return List.of();
        }

        Map<Long, List<Desempenho>> porAluno = new HashMap<>();
        desempenhoRepository.findAll().forEach(d ->
            porAluno.computeIfAbsent(d.getAluno().getId(), ignored -> new ArrayList<>()).add(d));

        PriorityQueue<RankingItemDTO> heap = new PriorityQueue<>();
        for (List<Desempenho> desempenhos : porAluno.values()) {
            RankingItemDTO item = calcularItem(desempenhos);
            heap.offer(item);
            if (heap.size() > top) {
                heap.poll();
            }
        }

        List<RankingItemDTO> ordenado = new ArrayList<>(heap);
        ordenado.sort(Comparator.comparingDouble(RankingItemDTO::mediaGeral).reversed()
            .thenComparing(RankingItemDTO::nome));

        List<RankingItemDTO> resultado = new ArrayList<>();
        for (int i = 0; i < ordenado.size(); i++) {
            RankingItemDTO item = ordenado.get(i);
            resultado.add(new RankingItemDTO(
                i + 1,
                item.nome(),
                item.matricula(),
                item.mediaGeral(),
                item.disciplinasConcluidas()
            ));
        }
        return List.copyOf(resultado);
    }

    @CacheEvict(value = "ranking", allEntries = true)
    public void invalidarRanking() {
    }

    private RankingItemDTO calcularItem(List<Desempenho> desempenhos) {
        Aluno aluno = desempenhos.get(0).getAluno();
        double media = desempenhos.stream()
            .map(Desempenho::getNotaFinal)
            .mapToDouble(nota -> nota == null ? 0.0 : nota.doubleValue())
            .average()
            .orElse(0.0);

        return new RankingItemDTO(
            0,
            aluno.getNome(),
            aluno.getMatricula(),
            media,
            desempenhos.size()
        );
    }
}
