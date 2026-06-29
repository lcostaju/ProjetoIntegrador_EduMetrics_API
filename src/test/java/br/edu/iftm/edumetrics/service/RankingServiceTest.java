package br.edu.iftm.edumetrics.service;

import br.edu.iftm.edumetrics.domain.Aluno;
import br.edu.iftm.edumetrics.domain.Desempenho;
import br.edu.iftm.edumetrics.domain.Disciplina;
import br.edu.iftm.edumetrics.domain.dto.RankingItemDTO;
import br.edu.iftm.edumetrics.repository.DesempenhoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {
    @Mock
    private DesempenhoRepository desempenhoRepository;

    @InjectMocks
    private RankingService rankingService;

    @Test
    void topKDeveRetornarAlunosComMaioresMedias() {
        Aluno ana = new Aluno(1L, "202600000001", "Ana", "ana@iftm.edu.br", "TSI", 5);
        Aluno bruno = new Aluno(2L, "202600000002", "Bruno", "bruno@iftm.edu.br", "TSI", 5);
        Aluno carla = new Aluno(3L, "202600000003", "Carla", "carla@iftm.edu.br", "TSI", 5);
        Disciplina disciplina = new Disciplina(1L, "ED01", "Estruturas de Dados", 4);

        when(desempenhoRepository.findAll()).thenReturn(List.of(
            desempenho(ana, disciplina, 8.0),
            desempenho(bruno, disciplina, 9.5),
            desempenho(carla, disciplina, 7.0)
        ));

        List<RankingItemDTO> ranking = rankingService.topK(2);

        assertEquals(2, ranking.size());
        assertEquals("Bruno", ranking.get(0).nome());
        assertEquals("Ana", ranking.get(1).nome());
        assertEquals(1, ranking.get(0).posicao());
    }

    @Test
    void topKComKIgualA1DeveRetornarApenasOMelhor() {
        Aluno ana = new Aluno(1L, "202600000001", "Ana", "ana@iftm.edu.br", "TSI", 5);
        Aluno bruno = new Aluno(2L, "202600000002", "Bruno", "bruno@iftm.edu.br", "TSI", 5);
        Disciplina disciplina = new Disciplina(1L, "ED01", "Estruturas de Dados", 4);

        when(desempenhoRepository.findAll()).thenReturn(List.of(
            desempenho(ana, disciplina, 8.0),
            desempenho(bruno, disciplina, 9.5)
        ));

        List<RankingItemDTO> ranking = rankingService.topK(1);

        assertEquals(1, ranking.size());
        assertEquals("Bruno", ranking.get(0).nome());
        assertEquals(1, ranking.get(0).posicao());
    }

    @Test
    void topKDeveTratarEmpateSemExcecao() {
        Aluno ana = new Aluno(1L, "202600000001", "Ana", "ana@iftm.edu.br", "TSI", 5);
        Aluno bruno = new Aluno(2L, "202600000002", "Bruno", "bruno@iftm.edu.br", "TSI", 5);
        Disciplina disciplina = new Disciplina(1L, "ED01", "Estruturas de Dados", 4);

        when(desempenhoRepository.findAll()).thenReturn(List.of(
            desempenho(ana, disciplina, 8.0),
            desempenho(bruno, disciplina, 8.0)
        ));

        List<RankingItemDTO> ranking = assertDoesNotThrow(() -> rankingService.topK(2));

        assertEquals(2, ranking.size());
        assertEquals(8.0, ranking.get(0).mediaGeral());
        assertEquals(8.0, ranking.get(1).mediaGeral());
    }

    @Test
    void invalidarRankingNaoDeveLancarExcecao() {
        assertDoesNotThrow(rankingService::invalidarRanking);
    }

    private Desempenho desempenho(Aluno aluno, Disciplina disciplina, double notaFinal) {
        Desempenho desempenho = new Desempenho();
        desempenho.setAluno(aluno);
        desempenho.setDisciplina(disciplina);
        desempenho.setNotaFinal(BigDecimal.valueOf(notaFinal));
        desempenho.setSemestre("2026/1");
        return desempenho;
    }
}
