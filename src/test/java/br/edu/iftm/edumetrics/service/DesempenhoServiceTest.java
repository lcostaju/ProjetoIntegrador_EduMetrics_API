package br.edu.iftm.edumetrics.service;

import br.edu.iftm.edumetrics.domain.Aluno;
import br.edu.iftm.edumetrics.domain.Desempenho;
import br.edu.iftm.edumetrics.domain.Disciplina;
import br.edu.iftm.edumetrics.domain.dto.DesempenhoDTO;
import br.edu.iftm.edumetrics.domain.dto.DesempenhoRequest;
import br.edu.iftm.edumetrics.repository.AlunoRepository;
import br.edu.iftm.edumetrics.repository.DesempenhoRepository;
import br.edu.iftm.edumetrics.repository.DisciplinaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesempenhoServiceTest {
    @Mock
    private DesempenhoRepository desempenhoRepository;
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private DisciplinaRepository disciplinaRepository;

    @InjectMocks
    private DesempenhoService desempenhoService;

    @Test
    void deveRegistrarDesempenhoCalculandoMediaFinal() {
        Aluno aluno = new Aluno(1L, "202600000001", "Ana", "ana@iftm.edu.br", "TSI", 5);
        Disciplina disciplina = new Disciplina(1L, "ED01", "Estruturas de Dados", 4);
        DesempenhoRequest request = new DesempenhoRequest(
            1L,
            "ED01",
            BigDecimal.valueOf(8.5),
            BigDecimal.valueOf(9.0),
            "2026/1"
        );

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findByCodigo("ED01")).thenReturn(Optional.of(disciplina));
        when(desempenhoRepository.save(org.mockito.ArgumentMatchers.any(Desempenho.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        DesempenhoDTO resultado = desempenhoService.registrar(request);

        assertEquals("Estruturas de Dados", resultado.disciplina());
        assertEquals(BigDecimal.valueOf(8.75).setScale(2), resultado.notaFinal());
    }

    @Test
    void deveRegistrarBuscaDisciplinaPorNomeCasoNaoAchePorCodigo() {
        Aluno aluno = new Aluno(1L, "202600000001", "Ana", "ana@iftm.edu.br", "TSI", 5);
        Disciplina disciplina = new Disciplina(1L, "ED01", "Estruturas de Dados", 4);
        DesempenhoRequest request = new DesempenhoRequest(
            1L,
            "Estruturas de Dados", // nome inves de codigo
            BigDecimal.valueOf(7.0),
            BigDecimal.valueOf(7.0),
            "2026/1"
        );

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findByCodigo("Estruturas de Dados")).thenReturn(Optional.empty());
        when(disciplinaRepository.findByNomeIgnoreCase("Estruturas de Dados")).thenReturn(Optional.of(disciplina));
        when(desempenhoRepository.save(org.mockito.ArgumentMatchers.any(Desempenho.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        DesempenhoDTO resultado = desempenhoService.registrar(request);

        assertEquals("Estruturas de Dados", resultado.disciplina());
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoEncontrado() {
        DesempenhoRequest request = new DesempenhoRequest(99L, "ED01", BigDecimal.ONE, BigDecimal.ONE, "2026/1");
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> desempenhoService.registrar(request));
    }

    @Test
    void deveLancarExcecaoQuandoDisciplinaNaoEncontrada() {
        Aluno aluno = new Aluno(1L, "202600000001", "Ana", "ana@iftm.edu.br", "TSI", 5);
        DesempenhoRequest request = new DesempenhoRequest(1L, "INVALIDA", BigDecimal.ONE, BigDecimal.ONE, "2026/1");
        
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(disciplinaRepository.findByCodigo("INVALIDA")).thenReturn(Optional.empty());
        when(disciplinaRepository.findByNomeIgnoreCase("INVALIDA")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> desempenhoService.registrar(request));
    }

    @Test
    void deveListarPorAluno() {
        Aluno aluno = new Aluno(1L, "202600000001", "Ana", "ana@iftm.edu.br", "TSI", 5);
        Disciplina disciplina = new Disciplina(1L, "ED01", "Estruturas de Dados", 4);
        Desempenho desemp = new Desempenho();
        desemp.setAluno(aluno);
        desemp.setDisciplina(disciplina);
        desemp.setNota1(BigDecimal.ONE);
        desemp.setNota2(BigDecimal.ONE);
        desemp.setNotaFinal(BigDecimal.ONE);
        desemp.setSemestre("2026/1");

        when(desempenhoRepository.findByAlunoId(1L)).thenReturn(List.of(desemp));

        List<DesempenhoDTO> resultados = desempenhoService.listarPorAluno(1L);
        assertEquals(1, resultados.size());
        assertEquals("Estruturas de Dados", resultados.get(0).disciplina());
    }
}
