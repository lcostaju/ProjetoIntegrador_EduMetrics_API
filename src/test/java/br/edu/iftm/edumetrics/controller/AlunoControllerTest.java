package br.edu.iftm.edumetrics.controller;

import br.edu.iftm.edumetrics.domain.dto.AlunoDTO;
import br.edu.iftm.edumetrics.service.AlunoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AlunoControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void postAlunosDeveRetornarCreated() throws Exception {
        AlunoService service = mock(AlunoService.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new AlunoController(service))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

        AlunoDTO request = new AlunoDTO(null, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5);
        AlunoDTO response = new AlunoDTO(1L, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5);
        when(service.cadastrar(request)).thenReturn(response);

        mvc.perform(post("/api/alunos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.matricula").value("202600000010"));
    }

    @Test
    void getAlunoPorIdDeveRetornarOk() throws Exception {
        AlunoService service = mock(AlunoService.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new AlunoController(service))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

        when(service.buscarPorId(1L))
            .thenReturn(new AlunoDTO(1L, "202600000010", "Maria", "maria@iftm.edu.br", "TSI", 5));

        mvc.perform(get("/api/alunos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Maria"));
    }
}
