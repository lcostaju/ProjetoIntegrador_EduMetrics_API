package br.edu.iftm.edumetrics.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record DesempenhoDTO(
    String disciplina,
    BigDecimal nota1,
    BigDecimal nota2,
    BigDecimal notaFinal,
    String semestre
) implements Serializable {}