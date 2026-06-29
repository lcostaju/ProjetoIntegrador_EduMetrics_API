package br.edu.iftm.edumetrics.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RelatorioRequest(
    @NotNull
    Long alunoId,
    @NotBlank String tipo,
    String semestre
) {}
