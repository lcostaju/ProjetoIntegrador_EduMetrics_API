package br.edu.iftm.edumetrics.estruturas;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrieTest {
    @Test
    void deveAutocompletarComPrefixoCaseInsensitive() {
        Trie trie = new Trie();
        trie.inserir("Estruturas de Dados");
        trie.inserir("Engenharia de Software");

        List<String> resultado = trie.autocompletar("estr", 10);

        assertEquals(List.of("Estruturas de Dados"), resultado);
    }

    @Test
    void prefixoInexistenteRetornaVazio() {
        Trie trie = new Trie();
        trie.inserir("Banco de Dados");

        assertTrue(trie.autocompletar("Web", 10).isEmpty());
    }

    @Test
    void deveRespeitarLimiteENaoDuplicarPalavra() {
        Trie trie = new Trie();
        trie.inserir("Programacao Web");
        trie.inserir("Programacao Web");
        trie.inserir("Programacao Mobile");

        List<String> resultado = trie.autocompletar("Programacao", 1);

        assertEquals(1, resultado.size());
        assertEquals(2, trie.size());
    }
}
