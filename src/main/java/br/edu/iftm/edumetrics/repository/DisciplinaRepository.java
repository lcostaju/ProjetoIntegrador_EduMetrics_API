package br.edu.iftm.edumetrics.repository;

import br.edu.iftm.edumetrics.domain.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    Optional<Disciplina> findByCodigo(String codigo);
    Optional<Disciplina> findByNomeIgnoreCase(String nome);
}
