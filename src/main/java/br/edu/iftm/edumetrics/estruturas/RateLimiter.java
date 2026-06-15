package br.edu.iftm.edumetrics.estruturas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiter com algoritmo Sliding Window.
 * Cada cliente tem um Deque<Long> de timestamps das requisições.
 * Ao processar uma nova requisição, timestamps expirados são removidos
 * e o tamanho atual é comparado ao limite.
 *
 * Mais preciso que Fixed Window Counter (sem burst no limite da janela).
 */
@Component
public class RateLimiter {

    // ConcurrentHashMap garante thread-safety sem bloqueio global
    private final ConcurrentHashMap<String, Deque<Long>> janelas = new ConcurrentHashMap<>();

    // Configurável via application.properties
    @Value("${rate.limiter.max-requisicoes:100}")
    private int maxRequisicoes;

    @Value("${rate.limiter.janela-ms:60000}")
    private long janelaMilissegundos;

    /**
     * Verifica se o cliente pode fazer a requisição.
     *
     * @param clienteId identificador do cliente (IP, userId, API key)
     * @return true se dentro do limite; false se excedeu
     */
    public boolean permitir(String clienteId) {
        long agora = System.currentTimeMillis();

        Deque<Long> timestamps = janelas.computeIfAbsent(
                clienteId, k -> new ArrayDeque<>());

        synchronized (timestamps) {
            // Remove timestamps fora da janela deslizante
            while (!timestamps.isEmpty() && agora - timestamps.peekFirst() > janelaMilissegundos) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= maxRequisicoes) {
                return false; // limite excedido — 429 Too Many Requests
            }

            timestamps.addLast(agora);
            return true;
        }
    }

    /** Retorna estatísticas para o endpoint /admin/rate-limiter/stats */
    public Map<String, Object> stats(String clienteId) {
        Deque<Long> ts = janelas.getOrDefault(clienteId, new ArrayDeque<>());
        return Map.of(
                "cliente", clienteId,
                "requisicoes_usadas", ts.size(),
                "limite", maxRequisicoes,
                "janela_ms", janelaMilissegundos
        );
    }
}