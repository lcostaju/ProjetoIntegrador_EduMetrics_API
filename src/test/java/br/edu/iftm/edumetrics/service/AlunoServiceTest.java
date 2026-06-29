package br.edu.iftm.edumetrics.service;

import br.edu.iftm.edumetrics.domain.Aluno;
import br.edu.iftm.edumetrics.domain.dto.AlunoDTO;
import br.edu.iftm.edumetrics.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {
    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private AutocompletarService autocompletarService;

    @InjectMocks
    private AlunoService alunoService;

    @Test
    void deveCadastrarAlunoQuandoMatriculaEEmailSaoUnicos() {
        AlunoDTO request = new AlunoDTO(null, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5);
        Aluno salvo = new Aluno(1L, request.matricula(), request.nome(), request.email(), request.curso(), request.periodo());

        when(alunoRepository.existsByMatricula(request.matricula())).thenReturn(false);
        when(alunoRepository.existsByEmail(request.email())).thenReturn(false);
        when(alunoRepository.save(org.mockito.ArgumentMatchers.any(Aluno.class))).thenReturn(salvo);

        AlunoDTO resultado = alunoService.cadastrar(request);

        assertEquals(1L, resultado.id());
        assertEquals(request.matricula(), resultado.matricula());
    }

    @Test
    void deveIndexarNomeNaTrieAoCadastrar() {
        AlunoDTO request = new AlunoDTO(null, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5);
        Aluno salvo = new Aluno(1L, request.matricula(), request.nome(), request.email(), request.curso(), request.periodo());

        when(alunoRepository.existsByMatricula(request.matricula())).thenReturn(false);
        when(alunoRepository.existsByEmail(request.email())).thenReturn(false);
        when(alunoRepository.save(org.mockito.ArgumentMatchers.any(Aluno.class))).thenReturn(salvo);

        alunoService.cadastrar(request);

        verify(autocompletarService).indexar("Maria");
    }

    @Test
    void deveRejeitarMatriculaDuplicada() {
        AlunoDTO request = new AlunoDTO(null, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5);
        when(alunoRepository.existsByMatricula(request.matricula())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> alunoService.cadastrar(request));
        verify(alunoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        AlunoDTO request = new AlunoDTO(null, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5);
        when(alunoRepository.existsByMatricula(request.matricula())).thenReturn(false);
        when(alunoRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> alunoService.cadastrar(request));
        verify(alunoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void buscaPorMatriculaDeveUsarCacheLocalNaSegundaChamada() {
        Aluno aluno = new Aluno(1L, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5);
        when(alunoRepository.findByMatricula("202600000010")).thenReturn(Optional.of(aluno));

        AlunoDTO primeira = alunoService.buscarPorMatricula("202600000010");
        AlunoDTO segunda = alunoService.buscarPorMatricula("202600000010");

        assertEquals(primeira, segunda);
        verify(alunoRepository, times(1)).findByMatricula("202600000010");
    }

    @Test
    void buscaPorMatriculaDeveLancarExcecaoQuandoNaoEncontrado() {
        when(alunoRepository.findByMatricula("invalida")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> alunoService.buscarPorMatricula("invalida"));
    }

    @Test
    void buscaPorIdDeveRetornarAluno() {
        Aluno aluno = new Aluno(1L, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        
        AlunoDTO resultado = alunoService.buscarPorId(1L);
        assertEquals("Maria", resultado.nome());
    }

    @Test
    void buscaPorIdDeveLancarExcecaoQuandoNaoEncontrado() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> alunoService.buscarPorId(99L));
    }

    @Test
    void deveAtualizarAlunoComSucesso() {
        Aluno aluno = new Aluno(1L, "202600000010", "Maria Antiga", "maria.old@iftm.edu.br", "TSI", 5);
        AlunoDTO request = new AlunoDTO(null, "202600000010", "Maria Nova", "maria.new@iftm.edu.br", "TSI", 6);
        
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(org.mockito.ArgumentMatchers.any(Aluno.class))).thenAnswer(i -> i.getArgument(0));

        AlunoDTO atualizado = alunoService.atualizar(1L, request);
        assertEquals("Maria Nova", atualizado.nome());
        assertEquals(6, atualizado.periodo());
    }

    @Test
    void atualizarDeveLancarExcecaoQuandoNaoEncontrado() {
        AlunoDTO request = new AlunoDTO(null, "202600000010", "Maria Nova", "maria.new@iftm.edu.br", "TSI", 6);
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> alunoService.atualizar(99L, request));
    }

    @Test
    void deveRemoverAlunoComSucesso() {
        Aluno aluno = new Aluno(1L, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        
        alunoService.remover(1L);
        verify(alunoRepository).delete(aluno);
    }

    @Test
    void removerDeveLancarExcecaoQuandoNaoEncontrado() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> alunoService.remover(99L));
    }

    @Test
    void deveLimparCacheDistribuido() {
        alunoService.limparCacheDistribuido();
        Map<String, Object> stats = alunoService.cacheStats();
        assertEquals(0, stats.get("lru_tamanho"));
        assertEquals(0L, stats.get("hits"));
        assertEquals(0L, stats.get("misses"));
    }
}
