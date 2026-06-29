package br.edu.iftm.edumetrics.config;

import br.edu.iftm.edumetrics.domain.Aluno;
import br.edu.iftm.edumetrics.domain.Disciplina;
import br.edu.iftm.edumetrics.repository.AlunoRepository;
import br.edu.iftm.edumetrics.repository.DisciplinaRepository;
import br.edu.iftm.edumetrics.service.AutocompletarService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private final DisciplinaRepository disciplinaRepository;
    private final AlunoRepository alunoRepository;
    private final AutocompletarService autocompletarService;

    public DataLoader(DisciplinaRepository disciplinaRepository,
                      AlunoRepository alunoRepository,
                      AutocompletarService autocompletarService) {
        this.disciplinaRepository = disciplinaRepository;
        this.alunoRepository = alunoRepository;
        this.autocompletarService = autocompletarService;
    }

    @Override
    public void run(String... args) {
        if (disciplinaRepository.count() == 0) {
            List<Disciplina> disciplinas = disciplinaRepository.saveAll(List.of(
                new Disciplina(null, "ED01", "Estruturas de Dados", 4),
                new Disciplina(null, "WEB01", "Programacao Web", 4),
                new Disciplina(null, "BD01", "Banco de Dados", 4),
                new Disciplina(null, "ENG01", "Engenharia de Software", 4)
            ));
            disciplinas.forEach(autocompletarService::indexar);
        }

        if (alunoRepository.count() == 0) {
            alunoRepository.saveAll(List.of(
                new Aluno(null, "202600000001", "Ana Silva", "ana.silva@iftm.edu.br", "TSI", 5),
                new Aluno(null, "202600000002", "Bruno Costa", "bruno.costa@iftm.edu.br", "TSI", 5),
                new Aluno(null, "202600000003", "Carla Souza", "carla.souza@iftm.edu.br", "TSI", 5)
            ));
        }
    }
}
