package br.edu.iftm.edumetrics.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DesempenhoTest {

    @Test
    void testGettersAndSetters() {
        Desempenho desempenho = new Desempenho();
        desempenho.setId(1L);
        
        Aluno aluno = new Aluno();
        aluno.setId(10L);
        desempenho.setAluno(aluno);
        
        Disciplina disciplina = new Disciplina();
        disciplina.setId(20L);
        desempenho.setDisciplina(disciplina);
        
        desempenho.setNota1(new BigDecimal("8.5"));
        desempenho.setNota2(new BigDecimal("9.5"));
        desempenho.setNotaFinal(new BigDecimal("9.0"));
        desempenho.setSemestre("2026/1");

        assertEquals(1L, desempenho.getId());
        assertEquals(10L, desempenho.getAluno().getId());
        assertEquals(20L, desempenho.getDisciplina().getId());
        assertEquals(new BigDecimal("8.5"), desempenho.getNota1());
        assertEquals(new BigDecimal("9.5"), desempenho.getNota2());
        assertEquals(new BigDecimal("9.0"), desempenho.getNotaFinal());
        assertEquals("2026/1", desempenho.getSemestre());
    }

    @Test
    void testEqualsAndHashCode() {
        Desempenho d1 = new Desempenho();
        d1.setId(1L);
        Desempenho d2 = new Desempenho();
        d2.setId(1L);
        Desempenho d3 = new Desempenho();
        d3.setId(2L);

        assertEquals(d1, d1);
        assertEquals(d1, d2);
        assertNotEquals(d1, d3);
        assertNotEquals(d1, null);
        assertNotEquals(d1, new Object());

        assertEquals(d1.hashCode(), d2.hashCode());
        assertNotEquals(d1.hashCode(), d3.hashCode());
    }
}
