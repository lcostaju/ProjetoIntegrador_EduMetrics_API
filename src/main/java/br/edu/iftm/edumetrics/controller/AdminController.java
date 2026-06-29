package br.edu.iftm.edumetrics.controller;

import br.edu.iftm.edumetrics.estruturas.RateLimiter;
import br.edu.iftm.edumetrics.service.AlunoService;
import br.edu.iftm.edumetrics.service.AutocompletarService;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AlunoService alunoService;
    private final AutocompletarService autocompletarService;
    private final RateLimiter rateLimiter;
    private final RedisConnectionFactory redisConnectionFactory;
    private final ConnectionFactory rabbitConnectionFactory;

    public AdminController(AlunoService alunoService,
                           AutocompletarService autocompletarService,
                           RateLimiter rateLimiter,
                           RedisConnectionFactory redisConnectionFactory,
                           ConnectionFactory rabbitConnectionFactory) {
        this.alunoService = alunoService;
        this.autocompletarService = autocompletarService;
        this.rateLimiter = rateLimiter;
        this.redisConnectionFactory = redisConnectionFactory;
        this.rabbitConnectionFactory = rabbitConnectionFactory;
    }

    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, Object>> cacheStats() {
        Map<String, Object> stats = new HashMap<>(alunoService.cacheStats());
        stats.put("trie_total_indexado", autocompletarService.totalIndexado());
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/cache")
    public ResponseEntity<Void> limparCache() {
        alunoService.limparCacheDistribuido();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rate-limiter/stats")
    public ResponseEntity<Map<String, Object>> rateLimiterStats(
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor) {
        String cliente = forwardedFor == null ? "local" : forwardedFor.split(",")[0].trim();
        return ResponseEntity.ok(rateLimiter.stats(cliente));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("redis", verificarRedis());
        status.put("rabbitmq", verificarRabbitMQ());
        status.put("trie_total_indexado", autocompletarService.totalIndexado());
        return ResponseEntity.ok(status);
    }

    private String verificarRedis() {
        try (var connection = redisConnectionFactory.getConnection()) {
            return "PONG".equalsIgnoreCase(connection.ping()) ? "UP" : "DOWN";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    private String verificarRabbitMQ() {
        try (var connection = rabbitConnectionFactory.createConnection()) {
            return connection.isOpen() ? "UP" : "DOWN";
        } catch (Exception e) {
            return "DOWN";
        }
    }
}
