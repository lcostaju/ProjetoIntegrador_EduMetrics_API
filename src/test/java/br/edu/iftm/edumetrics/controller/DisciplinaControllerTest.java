package br.edu.iftm.edumetrics.controller;

import br.edu.iftm.edumetrics.service.AutocompletarService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DisciplinaControllerTest {
    @Test
    void autocompletarDeveRetornarListaDaTrie() throws Exception {
        AutocompletarService service = mock(AutocompletarService.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new DisciplinaController(service)).build();
        when(service.autocompletar("Estr", 10)).thenReturn(List.of("Estruturas de Dados"));

        mvc.perform(get("/api/disciplinas/autocompletar").param("q", "Estr"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("Estruturas de Dados"));
    }
}
