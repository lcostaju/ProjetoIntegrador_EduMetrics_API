package br.edu.iftm.edumetrics.domain.dto;

import java.io.Serializable;

public record RankingItemDTO(
    int posicao,
    String nome,
    String matricula,
    double mediaGeral,
    int disciplinasConcluidas
) implements Comparable<RankingItemDTO>, Serializable {

    @Override
    public int compareTo(RankingItemDTO outro) {
        // Ordem crescente de media — necessário para o min-heap do PriorityQueue
        return Double.compare(this.mediaGeral, outro.mediaGeral);
    }
}