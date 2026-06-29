package br.edu.iftm.edumetrics.service;

import br.edu.iftm.edumetrics.domain.Disciplina;
import br.edu.iftm.edumetrics.repository.DisciplinaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutocompletarServiceTest {

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @InjectMocks
    private AutocompletarService autocompletarService;

    @Test
    void deveCarregarDisciplinasNoStartup() {
        Disciplina d1 = new Disciplina(1L, "ED01", "Estrutura de Dados", 4);
        Disciplina d2 = new Disciplina(2L, "ALG01", "Algoritmos", 4);

        when(disciplinaRepository.findAll()).thenReturn(List.of(d1, d2));

        autocompletarService.carregarDisciplinas();

        assertEquals(2, autocompletarService.totalIndexado());
        assertTrue(autocompletarService.autocompletar("Est", 10).contains("Estrutura de Dados"));
        assertTrue(autocompletarService.autocompletar("Alg", 10).contains("Algoritmos"));
    }

    @Test
    void deveIndexarNovaDisciplina() {
        Disciplina d = new Disciplina(3L, "MAT01", "Matematica Discreta", 4);
        autocompletarService.indexar(d);

        assertEquals(1, autocompletarService.totalIndexado());
        List<String> resultado = autocompletarService.autocompletar("Mat", 10);
        assertEquals(1, resultado.size());
        assertEquals("Matematica Discreta", resultado.get(0));
    }
}
