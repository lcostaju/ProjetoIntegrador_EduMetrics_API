package br.edu.iftm.edumetrics.messaging;

import br.edu.iftm.edumetrics.domain.dto.EventoRelatorio;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RelatorioConsumerTest {
    private final RelatorioConsumer consumer = new RelatorioConsumer();

    @Test
    void deveProcessarSemErroQuandoTipoEValido() {
        EventoRelatorio evento = new EventoRelatorio(
            UUID.randomUUID().toString(), 1L, "BOLETIM", "2026/1", Instant.now());

        assertDoesNotThrow(() -> consumer.processar(evento));
    }

    @Test
    void deveLancarExcecaoQuandoTipoEDesconhecidoParaRotearParaDLQ() {
        EventoRelatorio evento = new EventoRelatorio(
            UUID.randomUUID().toString(), 1L, "TIPO_INEXISTENTE", "2026/1", Instant.now());

        assertThrows(IllegalArgumentException.class, () -> consumer.processar(evento));
    }

    @Test
    void processarDlqNaoDeveLancarExcecao() {
        EventoRelatorio evento = new EventoRelatorio(
            UUID.randomUUID().toString(), 1L, "TIPO_INEXISTENTE", "2026/1", Instant.now());

        assertDoesNotThrow(() -> consumer.processarDlq(evento));
    }
}
