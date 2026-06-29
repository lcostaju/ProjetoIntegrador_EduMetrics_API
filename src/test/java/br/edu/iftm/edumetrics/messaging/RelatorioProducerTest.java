package br.edu.iftm.edumetrics.messaging;

import br.edu.iftm.edumetrics.config.RabbitMQConfig;
import br.edu.iftm.edumetrics.domain.dto.EventoRelatorio;
import br.edu.iftm.edumetrics.domain.dto.RelatorioRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RelatorioProducerTest {
    @Test
    void devePublicarEventoNoRabbitMQERetornarCorrelationId() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RelatorioProducer producer = new RelatorioProducer(rabbitTemplate);
        RelatorioRequest request = new RelatorioRequest(1L, "BOLETIM", "2026/1");
        ArgumentCaptor<EventoRelatorio> captor = ArgumentCaptor.forClass(EventoRelatorio.class);

        String correlationId = producer.solicitar(request);

        assertFalse(correlationId.isBlank());
        verify(rabbitTemplate).convertAndSend(
            org.mockito.ArgumentMatchers.eq(RabbitMQConfig.EXCHANGE),
            org.mockito.ArgumentMatchers.eq(RabbitMQConfig.ROUTING_KEY_RELATORIOS),
            captor.capture()
        );
        assertEquals(correlationId, captor.getValue().correlationId());
        assertEquals("BOLETIM", captor.getValue().tipo());
    }
}
