# EduMetrics API

API RESTful para acompanhamento de desempenho academico, com estruturas de dados aplicadas a cache, autocompletar, ranking e protecao contra abuso de requisicoes.

## Requisitos

- Java 21+
- Maven 3.9+
- Docker e Docker Compose
- RabbitMQ (Fila)

## Executar

```bash
docker compose up -d
mvn spring-boot:run
```

Servicos:

- API: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
- RabbitMQ Management: `http://localhost:15672` com `guest/guest`

## Testes e build

```bash
mvn test
mvn package -DskipTests
```

O relatorio JaCoCo e gerado em `target/site/jacoco/index.html` apos `mvn test`.

## Endpoints

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| POST | `/api/alunos` | Cadastra aluno |
| GET | `/api/alunos/{id}` | Busca aluno por id com cache Redis |
| GET | `/api/alunos/matricula/{matricula}` | Busca por matricula usando LRU local |
| PUT | `/api/alunos/{id}` | Atualiza aluno e cache |
| DELETE | `/api/alunos/{id}` | Remove aluno e invalida cache |
| GET | `/api/disciplinas/autocompletar?q={q}` | Autocompleta disciplinas via Trie |
| POST | `/api/desempenhos` | Registra notas e invalida ranking |
| GET | `/api/alunos/{id}/desempenho` | Lista desempenho do aluno |
| GET | `/api/ranking?top={k}` | Ranking Top-K por PriorityQueue |
| POST | `/api/relatorios` | Publica solicitacao de relatorio no RabbitMQ |
| GET | `/api/admin/cache/stats` | Estatisticas de cache local e Trie |
| DELETE | `/api/admin/cache` | Limpa caches da aplicacao |
| GET | `/api/admin/rate-limiter/stats` | Estatisticas do rate limiter |
| GET | `/api/admin/health` | Status de Redis, RabbitMQ e total indexado na Trie |

## Exemplos de payload

Cadastrar aluno:

```json
{
  "matricula": "202600000010",
  "nome": "Maria Oliveira",
  "email": "maria.oliveira@iftm.edu.br",
  "curso": "TSI",
  "periodo": 5
}
```

Registrar desempenho:

```json
{
  "alunoId": 1,
  "disciplina": "ED01",
  "nota1": 8.5,
  "nota2": 9.0,
  "semestre": "2026/1"
}
```

Solicitar relatorio:

```json
{
  "alunoId": 1,
  "tipo": "BOLETIM",
  "semestre": "2026/1"
}
```

## Estruturas implementadas

- `LRUCache<K,V>`: cache local baseado em `LinkedHashMap` com `accessOrder=true`.
- `Trie`: autocompletar de disciplinas por prefixo.
- `RateLimiter`: Sliding Window por cliente usando `ConcurrentHashMap` e `ArrayDeque`.
- `PriorityQueue`: ranking Top-K de alunos por media.

## Benchmark JMH

Classe: `br.edu.iftm.edumetrics.benchmark.EduMetricsBenchmark`. Configuracao: `N = 10_000` registros, 3 iteracoes de warmup e 5 de medicao (1s cada), 1 fork, modo `AverageTime` em `us/op`.

Gerar e executar (uber-jar via shade plugin):

```bash
mvn -Pbenchmarks -DskipTests package
java -jar target/benchmarks.jar -rf json -rff jmh-resultado.json
```

> Nota: nesta maquina o `maven-shade-plugin` apresentou um bug de configuracao com o Maven 3.9.11 instalado (erro `Cannot find 'resource' in class ManifestResourceTransformer`, ja reportado contra essa combinacao de versoes). Como alternativa equivalente, os benchmarks tambem podem ser executados direto do classpath, sem uber-jar:
>
> ```bash
> mvn dependency:build-classpath -Dmdep.outputFile=cp.txt -Dmdep.includeScope=runtime
> java -cp "target/classes;$(cat cp.txt)" org.openjdk.jmh.Main -rf json -rff jmh-resultado.json
> ```

Resultado real (`jmh-resultado.json`, gerado nesta maquina — JDK 22, `-Xms512m -Xmx1g`):

| Benchmark | Score | Erro +/- | Unidade | Analise |
| --- | ---: | ---: | --- | --- |
| buscaLRUCache | 0,070 | 0,028 | us/op | ~3,5x mais lento que o HashMap puro. O custo extra vem da lista duplamente encadeada que o `LinkedHashMap` com `accessOrder=true` precisa reordenar a cada `get()`. Ainda assim e O(1) amortizado e o overhead absoluto (~50 ns) e irrelevante perto da latencia de rede/IO que o cache substitui. |
| buscaHashMapDireto | 0,020 | 0,004 | us/op | Baseline O(1) sem nenhuma manutencao de ordem de acesso; serve de referencia para medir o overhead do LRU acima. |
| autocompletarTrie | 0,308 | 0,019 | us/op | Custo dominado pela descida de ~25 caracteres do prefixo de teste, independente do total de disciplinas cadastradas (Θ(\|prefixo\|)). Com 10.000 disciplinas ao inves de 6, esse valor nao mudaria, pois a Trie nao varre as palavras nao relacionadas ao prefixo. |
| autocompletarLinear | 5,043 | 0,377 | us/op | ~16x mais lento que a Trie com apenas 10.000 chaves no `HashMap` (varredura O(n) via stream + `startsWith`). Com 10.000 disciplinas reais (ao inves de matriculas de alunos usadas como proxy) essa diferenca cresceria linearmente com n, enquanto a Trie permaneceria praticamente constante — e exatamente o cenario que justifica trocar varredura linear por arvore de prefixos em producao. |
| rateLimiterPermitir | 0,057 | 0,025 | us/op | Bem abaixo de 1 us, confirmando o O(1) amortizado esperado do Sliding Window. O `synchronized` por cliente (nao um lock global) nao aparece como gargalo nessa escala; so se tornaria um problema sob alta contencao do mesmo `clienteId` em muitas threads simultaneas, cenario em que um `ReentrantLock`/`StampedLock` por cliente reduziria o tempo de espera sem mudar a complexidade. |

**Sobre a variancia:** os erros relativos (erro/score) ficam entre ~6% (Trie, varredura linear) e ~40-44% (LRU, RateLimiter). Isso nao indica instabilidade real do algoritmo — os scores absolutos estao na faixa de dezenas de nanosegundos, onde ruido de JIT (com poucas iteracoes de warmup), pausas pontuais de GC e jitter do agendador do SO dominam a medicao em termos relativos, mesmo com baixa variancia absoluta (desvio padrao < 0,03 us em todos os casos). Para reduzir ainda mais essa variancia seria necessario aumentar `@Fork` e `@Measurement` (o PDF sugere `Fork=2`, `Warmup=5x1s`, `Measurement=10x1s`; aqui foram usados valores menores para manter o benchmark rapido em ambiente de desenvolvimento).
