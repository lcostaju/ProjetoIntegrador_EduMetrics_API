package br.edu.iftm.edumetrics.estruturas;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {
    @Test
    void devePermitirDentroDoLimiteEBloquearAposLimite() {
        RateLimiter rateLimiter = new RateLimiter();
        ReflectionTestUtils.setField(rateLimiter, "maxRequisicoes", 2);
        ReflectionTestUtils.setField(rateLimiter, "janelaMilissegundos", 60_000L);

        assertTrue(rateLimiter.permitir("cliente-a"));
        assertTrue(rateLimiter.permitir("cliente-a"));
        assertFalse(rateLimiter.permitir("cliente-a"));
    }

    @Test
    void limitesDevemSerIndependentesPorCliente() {
        RateLimiter rateLimiter = new RateLimiter();
        ReflectionTestUtils.setField(rateLimiter, "maxRequisicoes", 1);
        ReflectionTestUtils.setField(rateLimiter, "janelaMilissegundos", 60_000L);

        assertTrue(rateLimiter.permitir("cliente-a"));
        assertFalse(rateLimiter.permitir("cliente-a"));
        assertTrue(rateLimiter.permitir("cliente-b"));
    }

    @Test
    void devePermitirNovamenteAposExpirarJanela() throws InterruptedException {
        RateLimiter rateLimiter = new RateLimiter();
        ReflectionTestUtils.setField(rateLimiter, "maxRequisicoes", 1);
        ReflectionTestUtils.setField(rateLimiter, "janelaMilissegundos", 5L);

        assertTrue(rateLimiter.permitir("cliente-a"));
        assertFalse(rateLimiter.permitir("cliente-a"));
        Thread.sleep(10L);
        assertTrue(rateLimiter.permitir("cliente-a"));
    }
}
