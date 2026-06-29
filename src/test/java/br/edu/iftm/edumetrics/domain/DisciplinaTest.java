package br.edu.iftm.edumetrics.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DisciplinaTest {

    @Test
    void testGettersAndSetters() {
        Disciplina disciplina = new Disciplina();
        disciplina.setId(1L);
        disciplina.setCodigo("ED01");
        disciplina.setNome("Estrutura de Dados");
        disciplina.setCreditos(4);

        assertEquals(1L, disciplina.getId());
        assertEquals("ED01", disciplina.getCodigo());
        assertEquals("Estrutura de Dados", disciplina.getNome());
        assertEquals(4, disciplina.getCreditos());
    }

    @Test
    void testConstructors() {
        Disciplina disciplina = new Disciplina(1L, "ED01", "Estrutura de Dados", 4);
        assertEquals(1L, disciplina.getId());
        assertEquals("ED01", disciplina.getCodigo());
        assertEquals("Estrutura de Dados", disciplina.getNome());
        assertEquals(4, disciplina.getCreditos());
    }

    @Test
    void testEqualsAndHashCode() {
        Disciplina d1 = new Disciplina(1L, "ED01", "Estrutura de Dados", 4);
        Disciplina d2 = new Disciplina(1L, "MAT01", "Matematica", 4);
        Disciplina d3 = new Disciplina(2L, "ED01", "Estrutura de Dados", 4);

        assertEquals(d1, d1);
        assertEquals(d1, d2); // mesmos IDs
        assertNotEquals(d1, d3);
        assertNotEquals(d1, null);
        assertNotEquals(d1, new Object());

        assertEquals(d1.hashCode(), d2.hashCode());
        assertNotEquals(d1.hashCode(), d3.hashCode());
    }
}
