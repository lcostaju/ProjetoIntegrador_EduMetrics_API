package br.edu.iftm.edumetrics.benchmark;

import br.edu.iftm.edumetrics.domain.dto.AlunoDTO;
import br.edu.iftm.edumetrics.estruturas.LRUCache;
import br.edu.iftm.edumetrics.estruturas.RateLimiter;
import br.edu.iftm.edumetrics.estruturas.Trie;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = {"-Xms512m", "-Xmx1g"})
public class EduMetricsBenchmark {
    private static final int N = 10_000;

    private Map<String, AlunoDTO> lruCache;
    private Map<String, AlunoDTO> hashMapIndice;
    private List<String> matriculas;
    private List<String> disciplinas;
    private Trie trie;
    private RateLimiter rateLimiter;

    @Setup
    public void setup() {
        lruCache = LRUCache.create(N);
        hashMapIndice = new HashMap<>();
        matriculas = new ArrayList<>();
        disciplinas = new ArrayList<>();
        trie = new Trie();
        rateLimiter = new RateLimiter();

        for (int i = 0; i < N; i++) {
            String matricula = "2026" + String.format("%08d", i);
            AlunoDTO aluno = new AlunoDTO(
                (long) i,
                matricula,
                "Aluno " + i,
                "aluno" + i + "@iftm.edu.br",
                "TSI",
                5
            );
            matriculas.add(matricula);
            lruCache.put(matricula, aluno);
            hashMapIndice.put(matricula, aluno);

            String disciplina = "Disciplina de Estruturas " + i;
            disciplinas.add(disciplina);
            trie.inserir(disciplina);
        }
    }

    @Benchmark
    public AlunoDTO buscaLRUCache(Blackhole blackhole) {
        String matricula = matriculas.get(ThreadLocalRandom.current().nextInt(N));
        AlunoDTO dto = lruCache.get(matricula);
        blackhole.consume(dto);
        return dto;
    }

    @Benchmark
    public AlunoDTO buscaHashMapDireto(Blackhole blackhole) {
        String matricula = matriculas.get(ThreadLocalRandom.current().nextInt(N));
        AlunoDTO dto = hashMapIndice.get(matricula);
        blackhole.consume(dto);
        return dto;
    }

    @Benchmark
    public List<String> autocompletarTrie(Blackhole blackhole) {
        List<String> resultado = trie.autocompletar("Disciplina de Estruturas 99", 10);
        blackhole.consume(resultado);
        return resultado;
    }

    @Benchmark
    public List<String> autocompletarLinear(Blackhole blackhole) {
        List<String> resultado = disciplinas.stream()
            .filter(disciplina -> disciplina.startsWith("Disciplina de Estruturas 99"))
            .limit(10)
            .toList();
        blackhole.consume(resultado);
        return resultado;
    }

    @Benchmark
    public boolean rateLimiterPermitir(Blackhole blackhole) {
        boolean permitido = rateLimiter.permitir("cliente-" + ThreadLocalRandom.current().nextInt(N));
        blackhole.consume(permitido);
        return permitido;
    }
}
