package br.edu.iftm.edumetrics.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AlunoTest {

    @Test
    void testGettersAndSetters() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setMatricula("123");
        aluno.setNome("Joao");
        aluno.setEmail("joao@email.com");
        aluno.setCurso("TSI");
        aluno.setPeriodo(5);
        
        Desempenho desempenho = new Desempenho();
        aluno.setDesempenhos(new ArrayList<>());
        aluno.getDesempenhos().add(desempenho);

        assertEquals(1L, aluno.getId());
        assertEquals("123", aluno.getMatricula());
        assertEquals("Joao", aluno.getNome());
        assertEquals("joao@email.com", aluno.getEmail());
        assertEquals("TSI", aluno.getCurso());
        assertEquals(5, aluno.getPeriodo());
        assertEquals(1, aluno.getDesempenhos().size());
    }

    @Test
    void testConstructors() {
        Aluno aluno = new Aluno(1L, "123", "Joao", "joao@email.com", "TSI", 5);
        assertEquals("Joao", aluno.getNome());
        assertNotNull(aluno.getDesempenhos());
    }

    @Test
    void testEqualsAndHashCode() {
        Aluno a1 = new Aluno(1L, "123", "Joao", "joao@email.com", "TSI", 5);
        Aluno a2 = new Aluno(1L, "456", "Maria", "maria@email.com", "TSI", 5);
        Aluno a3 = new Aluno(2L, "123", "Joao", "joao@email.com", "TSI", 5);

        assertEquals(a1, a1);
        assertEquals(a1, a2); // mesmos IDs
        assertNotEquals(a1, a3);
        assertNotEquals(a1, null);
        assertNotEquals(a1, new Object());

        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1.hashCode(), a3.hashCode());
    }
}
