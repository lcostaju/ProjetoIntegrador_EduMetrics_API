package br.edu.iftm.edumetrics.domain.dto;

import java.io.Serializable;
import java.time.Instant;

// Mensagem enviada ao RabbitMQ — deve ser serializável (Jackson)
public record EventoRelatorio(
    String correlationId, // UUID para rastrear o relatorio
    Long alunoId,
    String tipo,          // "BOLETIM", "HISTORICO", "RANKING"
    String semestre,
    Instant solicitadoEm
) implements Serializable {}