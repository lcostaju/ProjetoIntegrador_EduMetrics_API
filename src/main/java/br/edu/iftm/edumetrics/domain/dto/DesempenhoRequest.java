package br.edu.iftm.edumetrics.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record DesempenhoRequest(
    @NotNull Long alunoId,
    @NotBlank String disciplina,
    @DecimalMin("0.0") @DecimalMax("10.0")
    @NotNull BigDecimal nota1,
    @DecimalMin("0.0") @DecimalMax("10.0")
    @NotNull BigDecimal nota2,
    @NotBlank String semestre
) {}
