package br.edu.iftm.edumetrics.repository;

import br.edu.iftm.edumetrics.domain.Aluno;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    boolean existsByMatricula(String matricula);
    boolean existsByEmail(String email);
    Optional<Aluno> findByMatricula(String matricula);

    @EntityGraph(attributePaths = "desempenhos")
    Optional<Aluno> findWithDesempenhosById(Long id);
}
