package br.edu.iftm.edumetrics.repository;

import br.edu.iftm.edumetrics.domain.Desempenho;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesempenhoRepository extends JpaRepository<Desempenho, Long> {
    @EntityGraph(attributePaths = {"aluno", "disciplina"})
    List<Desempenho> findByAlunoId(Long alunoId);

    @EntityGraph(attributePaths = {"aluno", "disciplina"})
    List<Desempenho> findAll();
}
