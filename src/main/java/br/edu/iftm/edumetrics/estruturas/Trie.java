package br.edu.iftm.edumetrics.estruturas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Árvore de prefixos para autocompletar nomes de disciplinas.
 * Cada nó representa um caractere; caminhos da raiz até nos terminais
 * formam as palavras inseridas.
 *
 * Complexidade de busca: Θ(|prefixo|) — independe do total de palavras.
 */
public class Trie {

    private static class No {
        final Map<Character, No> filhos = new HashMap<>();
        boolean fimDePalavra = false;
        String valorCompleto; // armazena o termo completo (com formatação original)
    }

    private final No raiz = new No();
    private int totalPalavras = 0;

    /**
     * Insere uma palavra na Trie.
     * Complexidade: Θ(|palavra|)
     */
    public void inserir(String palavra) {
        if (palavra == null || palavra.isBlank()) return;

        No atual = raiz;
        // Indexamos em minúsculo para busca case-insensitive
        for (char c : palavra.toLowerCase().toCharArray()) {
            atual = atual.filhos.computeIfAbsent(c, k -> new No());
        }

        if (!atual.fimDePalavra) {
            atual.fimDePalavra = true;
            atual.valorCompleto = palavra; // preserva formatação original
            totalPalavras++;
        }
    }

    /**
     * Retorna até maxResultados termos que começam com o prefixo.
     * Complexidade: Θ(|prefixo|) para a descida + O(k) para a coleta.
     *
     * @param prefixo string de busca (case-insensitive)
     * @param maxResultados limite superior de resultados
     * @return lista de termos correspondentes, em ordem de DFS
     */
    public List<String> autocompletar(String prefixo, int maxResultados) {
        if (prefixo == null || prefixo.isBlank()) return List.of();

        No no = descerAte(prefixo.toLowerCase());
        if (no == null) return List.of();

        List<String> resultados = new ArrayList<>();
        coletarPalavras(no, resultados, maxResultados);
        return Collections.unmodifiableList(resultados);
    }

    private No descerAte(String prefixo) {
        No atual = raiz;
        for (char c : prefixo.toCharArray()) {
            atual = atual.filhos.get(c);
            if (atual == null) return null;
        }
        return atual;
    }

    private void coletarPalavras(No no, List<String> resultado, int max) {
        if (resultado.size() >= max) return;
        if (no.fimDePalavra) resultado.add(no.valorCompleto);

        for (No filho : no.filhos.values()) {
            coletarPalavras(filho, resultado, max);
        }
    }

    public int size() { return totalPalavras; }
    public boolean isEmpty() { return totalPalavras == 0; }
}