package br.edu.iftm.edumetrics.messaging;

import br.edu.iftm.edumetrics.config.RabbitMQConfig;
import br.edu.iftm.edumetrics.domain.dto.EventoRelatorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RelatorioConsumer {
    private static final Logger log = LoggerFactory.getLogger(RelatorioConsumer.class);
    private static final Set<String> TIPOS_VALIDOS = Set.of("BOLETIM", "HISTORICO", "RANKING");

    @RabbitListener(queues = RabbitMQConfig.FILA_RELATORIOS)
    public void processar(EventoRelatorio evento) {
        if (!TIPOS_VALIDOS.contains(evento.tipo())) {
            // Lancar excecao aqui faz o Rabbit dar NACK na mensagem, que e roteada para a DLQ
            // configurada em RabbitMQConfig (x-dead-letter-exchange/routing-key)
            throw new IllegalArgumentException("Tipo de relatorio desconhecido: " + evento.tipo());
        }
        log.info("Relatorio {} solicitado para aluno {}. correlationId={}",
            evento.tipo(), evento.alunoId(), evento.correlationId());
    }

    @RabbitListener(queues = RabbitMQConfig.FILA_DLQ)
    public void processarDlq(EventoRelatorio evento) {
        log.error("Mensagem enviada para DLQ. tipo={}, aluno={}, correlationId={}",
            evento.tipo(), evento.alunoId(), evento.correlationId());
    }
}
