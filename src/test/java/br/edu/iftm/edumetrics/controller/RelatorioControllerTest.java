package br.edu.iftm.edumetrics.controller;

import br.edu.iftm.edumetrics.domain.dto.RelatorioRequest;
import br.edu.iftm.edumetrics.messaging.RelatorioProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RelatorioControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void postRelatoriosDeveRetornarAcceptedComCorrelationId() throws Exception {
        RelatorioProducer producer = mock(RelatorioProducer.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new RelatorioController(producer))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();
        RelatorioRequest request = new RelatorioRequest(1L, "BOLETIM", "2026/1");
        when(producer.solicitar(request)).thenReturn("corr-123");

        mvc.perform(post("/api/relatorios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.correlationId").value("corr-123"));
    }
}
