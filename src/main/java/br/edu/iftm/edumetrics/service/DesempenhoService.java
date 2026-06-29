package br.edu.iftm.edumetrics.service;

import br.edu.iftm.edumetrics.domain.Aluno;
import br.edu.iftm.edumetrics.domain.Desempenho;
import br.edu.iftm.edumetrics.domain.Disciplina;
import br.edu.iftm.edumetrics.domain.dto.DesempenhoDTO;
import br.edu.iftm.edumetrics.domain.dto.DesempenhoRequest;
import br.edu.iftm.edumetrics.repository.AlunoRepository;
import br.edu.iftm.edumetrics.repository.DesempenhoRepository;
import br.edu.iftm.edumetrics.repository.DisciplinaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DesempenhoService {
    private final DesempenhoRepository desempenhoRepository;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public DesempenhoService(DesempenhoRepository desempenhoRepository,
                             AlunoRepository alunoRepository,
                             DisciplinaRepository disciplinaRepository) {
        this.desempenhoRepository = desempenhoRepository;
        this.alunoRepository = alunoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional
    @CacheEvict(value = {"desempenhos", "ranking"}, allEntries = true)
    public DesempenhoDTO registrar(DesempenhoRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId())
            .orElseThrow(() -> new NoSuchElementException("Aluno nao encontrado: " + request.alunoId()));
        Disciplina disciplina = buscarDisciplina(request.disciplina());

        Desempenho desempenho = new Desempenho();
        desempenho.setAluno(aluno);
        desempenho.setDisciplina(disciplina);
        desempenho.setNota1(request.nota1());
        desempenho.setNota2(request.nota2());
        desempenho.setNotaFinal(calcularMedia(request.nota1(), request.nota2()));
        desempenho.setSemestre(request.semestre());

        return toDTO(desempenhoRepository.save(desempenho));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "desempenhos", key = "#alunoId")
    public List<DesempenhoDTO> listarPorAluno(Long alunoId) {
        return desempenhoRepository.findByAlunoId(alunoId).stream()
            .map(this::toDTO)
            .toList();
    }

    private Disciplina buscarDisciplina(String codigoOuNome) {
        return disciplinaRepository.findByCodigo(codigoOuNome)
            .or(() -> disciplinaRepository.findByNomeIgnoreCase(codigoOuNome))
            .orElseThrow(() -> new NoSuchElementException("Disciplina nao encontrada: " + codigoOuNome));
    }

    private BigDecimal calcularMedia(BigDecimal nota1, BigDecimal nota2) {
        return nota1.add(nota2).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    private DesempenhoDTO toDTO(Desempenho desempenho) {
        return new DesempenhoDTO(
            desempenho.getDisciplina().getNome(),
            desempenho.getNota1(),
            desempenho.getNota2(),
            desempenho.getNotaFinal(),
            desempenho.getSemestre()
        );
    }
}
