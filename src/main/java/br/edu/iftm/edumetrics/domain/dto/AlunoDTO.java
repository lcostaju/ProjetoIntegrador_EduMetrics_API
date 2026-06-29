package br.edu.iftm.edumetrics.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

// Records são imutáveis — ideais para cache (sem risco de mutação após armazenar)
public record AlunoDTO(
    Long id,
    @NotBlank @Size(max = 12)
    String matricula,
    @NotBlank @Size(max = 120)
    String nome,
    @NotBlank @Email @Size(max = 150)
    String email,
    @NotBlank
    String curso,
    @NotNull @Min(1) @Max(12)
    Integer periodo
) implements Serializable {}
