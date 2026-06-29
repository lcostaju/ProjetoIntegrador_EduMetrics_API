package br.edu.iftm.edumetrics.controller;

import br.edu.iftm.edumetrics.estruturas.RateLimiter;
import br.edu.iftm.edumetrics.service.AlunoService;
import br.edu.iftm.edumetrics.service.AutocompletarService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest {

    private AlunoService alunoService;
    private AutocompletarService autocompletarService;
    private RateLimiter rateLimiter;
    private RedisConnectionFactory redisConnectionFactory;
    private ConnectionFactory rabbitConnectionFactory;
    private MockMvc mvc;

    private void montarMvc() {
        alunoService = mock(AlunoService.class);
        autocompletarService = mock(AutocompletarService.class);
        rateLimiter = mock(RateLimiter.class);
        redisConnectionFactory = mock(RedisConnectionFactory.class);
        rabbitConnectionFactory = mock(ConnectionFactory.class);
        mvc = MockMvcBuilders.standaloneSetup(new AdminController(
                alunoService, autocompletarService, rateLimiter,
                redisConnectionFactory, rabbitConnectionFactory)).build();
    }

    @Test
    void cacheStatsDeveCombinarStatsDoAlunoServiceComTotalDaTrie() throws Exception {
        montarMvc();
        when(alunoService.cacheStats()).thenReturn(Map.of("lru_tamanho", 1, "hits", 1L, "misses", 0L));
        when(autocompletarService.totalIndexado()).thenReturn(5);

        mvc.perform(get("/api/admin/cache/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.trie_total_indexado").value(5))
            .andExpect(jsonPath("$.lru_tamanho").value(1));
    }

    @Test
    void deleteCacheDeveRetornarNoContent() throws Exception {
        montarMvc();
        mvc.perform(delete("/api/admin/cache"))
            .andExpect(status().isNoContent());
    }

    @Test
    void rateLimiterStatsDeveUsarIpLocalQuandoSemHeader() throws Exception {
        montarMvc();
        when(rateLimiter.stats("local")).thenReturn(Map.of("cliente", "local", "requisicoes_usadas", 0));

        mvc.perform(get("/api/admin/rate-limiter/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cliente").value("local"));
    }

    @Test
    void healthDeveRetornarUpQuandoRedisERabbitEstaoDisponiveis() throws Exception {
        montarMvc();
        RedisConnection redisConnection = mock(RedisConnection.class);
        when(redisConnection.ping()).thenReturn("PONG");
        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);

        Connection rabbitConnection = mock(Connection.class);
        when(rabbitConnection.isOpen()).thenReturn(true);
        when(rabbitConnectionFactory.createConnection()).thenReturn(rabbitConnection);

        when(autocompletarService.totalIndexado()).thenReturn(7);

        mvc.perform(get("/api/admin/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.redis").value("UP"))
            .andExpect(jsonPath("$.rabbitmq").value("UP"))
            .andExpect(jsonPath("$.trie_total_indexado").value(7));
    }

    @Test
    void healthDeveRetornarDownQuandoRedisERabbitFalham() throws Exception {
        montarMvc();
        when(redisConnectionFactory.getConnection()).thenThrow(new RuntimeException("sem conexao"));
        when(rabbitConnectionFactory.createConnection()).thenThrow(new RuntimeException("sem conexao"));
        when(autocompletarService.totalIndexado()).thenReturn(0);

        mvc.perform(get("/api/admin/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.redis").value("DOWN"))
            .andExpect(jsonPath("$.rabbitmq").value("DOWN"));
    }
}
