package br.edu.iftm.edumetrics.service;

import br.edu.iftm.edumetrics.domain.Aluno;
import br.edu.iftm.edumetrics.domain.dto.AlunoDTO;
import br.edu.iftm.edumetrics.estruturas.LRUCache;
import br.edu.iftm.edumetrics.repository.AlunoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final AutocompletarService autocompletarService;
    private final Map<String, AlunoDTO> cacheMatricula = LRUCache.create(500);
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();

    public AlunoService(AlunoRepository alunoRepository, AutocompletarService autocompletarService) {
        this.alunoRepository = alunoRepository;
        this.autocompletarService = autocompletarService;
    }

    @Transactional
    public AlunoDTO cadastrar(AlunoDTO dto) {
        validarNovoAluno(dto);
        Aluno salvo = alunoRepository.save(toEntity(dto));
        AlunoDTO alunoDTO = toDTO(salvo);
        cacheMatricula.put(alunoDTO.matricula(), alunoDTO);
        autocompletarService.indexar(alunoDTO.nome());
        return alunoDTO;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "alunos", key = "#id")
    public AlunoDTO buscarPorId(Long id) {
        return alunoRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new NoSuchElementException("Aluno nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public AlunoDTO buscarPorMatricula(String matricula) {
        AlunoDTO cached = cacheMatricula.get(matricula);
        if (cached != null) {
            hits.incrementAndGet();
            return cached;
        }

        misses.incrementAndGet();
        AlunoDTO dto = alunoRepository.findByMatricula(matricula)
            .map(this::toDTO)
            .orElseThrow(() -> new NoSuchElementException("Aluno nao encontrado: " + matricula));
        cacheMatricula.put(matricula, dto);
        return dto;
    }

    @Transactional
    @CachePut(value = "alunos", key = "#id")
    public AlunoDTO atualizar(Long id, AlunoDTO dto) {
        Aluno aluno = alunoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Aluno nao encontrado: " + id));

        aluno.setMatricula(dto.matricula());
        aluno.setNome(dto.nome());
        aluno.setEmail(dto.email());
        aluno.setCurso(dto.curso());
        aluno.setPeriodo(dto.periodo());

        AlunoDTO atualizado = toDTO(alunoRepository.save(aluno));
        cacheMatricula.put(atualizado.matricula(), atualizado);
        return atualizado;
    }

    @Transactional
    @CacheEvict(value = "alunos", key = "#id")
    public void remover(Long id) {
        Aluno aluno = alunoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Aluno nao encontrado: " + id));
        cacheMatricula.remove(aluno.getMatricula());
        alunoRepository.delete(aluno);
    }

    @CacheEvict(value = {"alunos", "desempenhos", "ranking"}, allEntries = true)
    public void limparCacheDistribuido() {
        cacheMatricula.clear();
        hits.set(0);
        misses.set(0);
    }

    public Map<String, Object> cacheStats() {
        return Map.of(
            "lru_tamanho", cacheMatricula.size(),
            "lru_capacidade", 500,
            "hits", hits.get(),
            "misses", misses.get()
        );
    }

    private void validarNovoAluno(AlunoDTO dto) {
        if (alunoRepository.existsByMatricula(dto.matricula())) {
            throw new IllegalArgumentException("Matricula ja cadastrada: " + dto.matricula());
        }
        if (alunoRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email ja cadastrado: " + dto.email());
        }
    }

    private Aluno toEntity(AlunoDTO dto) {
        return new Aluno(dto.id(), dto.matricula(), dto.nome(), dto.email(), dto.curso(), dto.periodo());
    }

    private AlunoDTO toDTO(Aluno aluno) {
        return new AlunoDTO(
            aluno.getId(),
            aluno.getMatricula(),
            aluno.getNome(),
            aluno.getEmail(),
            aluno.getCurso(),
            aluno.getPeriodo()
        );
    }
}
