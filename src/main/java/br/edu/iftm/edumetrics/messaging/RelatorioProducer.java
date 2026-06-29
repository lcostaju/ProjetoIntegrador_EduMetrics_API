package br.edu.iftm.edumetrics.messaging;

import br.edu.iftm.edumetrics.config.RabbitMQConfig;
import br.edu.iftm.edumetrics.domain.dto.EventoRelatorio;
import br.edu.iftm.edumetrics.domain.dto.RelatorioRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RelatorioProducer {
    private final RabbitTemplate rabbitTemplate;

    public RelatorioProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String solicitar(RelatorioRequest request) {
        String correlationId = UUID.randomUUID().toString();
        EventoRelatorio evento = new EventoRelatorio(
            correlationId,
            request.alunoId(),
            request.tipo(),
            request.semestre(),
            Instant.now()
        );

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.ROUTING_KEY_RELATORIOS,
            evento
        );
        return correlationId;
    }
}
