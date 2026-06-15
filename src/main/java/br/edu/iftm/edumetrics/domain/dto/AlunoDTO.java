package br.edu.iftm.edumetrics.domain.dto;

import java.io.Serializable;

// Records são imutáveis — ideais para cache (sem risco de mutação após armazenar)
public record AlunoDTO(
    Long id,
    String matricula,
    String nome,
    String email,
    String curso,
    Integer periodo
) implements Serializable {}