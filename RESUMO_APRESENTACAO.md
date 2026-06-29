# Resumo de Apresentacao - EduMetrics API

Este documento resume o que foi implementado no repositorio e o que deve ser apresentado conforme o PDF `ProjetoIntegrador_EduMetrics_API.pdf`.

## 1. Objetivo do Projeto

O EduMetrics API e uma API REST para acompanhamento de desempenho academico. O ponto central do projeto e demonstrar que as estruturas de dados escolhidas resolvem problemas reais de performance:

- Consulta rapida de aluno por cache.
- Busca de disciplina por prefixo usando Trie.
- Ranking Top-K com PriorityQueue.
- Relatorios assincronos com RabbitMQ.
- Protecao contra abuso de API com Sliding Window Rate Limiter.
- Medicao de desempenho com JMH.

## 2. O Que Foi Implementado

### Infraestrutura

- Projeto Spring Boot com Java 21.
- Banco H2 em memoria para desenvolvimento.
- Docker Compose com Redis 7 e RabbitMQ 3.
- Configuracao Redis para cache com TTL.
- Configuracao RabbitMQ com exchange, fila principal e DLQ.
- DataLoader com alunos e disciplinas iniciais.

Arquivos principais:

- `docker-compose.yml`
- `src/main/resources/application.properties`
- `src/main/java/br/edu/iftm/edumetrics/config/CacheConfig.java`
- `src/main/java/br/edu/iftm/edumetrics/config/RabbitMQConfig.java`
- `src/main/java/br/edu/iftm/edumetrics/config/DataLoader.java`

### Modelo de Dados

- `Aluno`: matricula, nome, email, curso, periodo e lista de desempenhos.
- `Disciplina`: codigo, nome e creditos.
- `Desempenho`: aluno, disciplina, notas, media final e semestre.
- DTOs imutaveis com `record`, adequados para cache e transporte JSON.

### Estruturas de Dados

- `LRUCache<K,V>`: implementado com `LinkedHashMap` em modo `accessOrder=true`, com eviccao do item menos recentemente usado.
- `Trie`: arvore de prefixos para autocompletar disciplinas em tempo proporcional ao tamanho do prefixo.
- `RateLimiter`: Sliding Window com `ConcurrentHashMap<String, Deque<Long>>`, limitando requisicoes por cliente.
- `PriorityQueue`: usada no `RankingService` para obter Top-K alunos em `O(n log k)`.

### Camadas da API

- Repositories JPA:
  - `AlunoRepository`
  - `DisciplinaRepository`
  - `DesempenhoRepository`

- Services:
  - `AlunoService`: CRUD, cache Redis, LRU local por matricula e estatisticas.
  - `AutocompletarService`: carrega disciplinas na Trie e responde autocompletar.
  - `DesempenhoService`: registra notas e calcula media final.
  - `RankingService`: monta ranking Top-K com PriorityQueue.
  - `RelatorioProducer`: publica eventos no RabbitMQ.
  - `RelatorioConsumer`: consome fila principal e DLQ.

- Controllers REST:
  - `AlunoController`
  - `DisciplinaController`
  - `DesempenhoController`
  - `RelatorioController`
  - `AdminController`
  - `ApiExceptionHandler`

### Endpoints Implementados

| Metodo | Endpoint | O que demonstrar |
| --- | --- | --- |
| POST | `/api/alunos` | Cadastro com validacao |
| GET | `/api/alunos/{id}` | Busca por id usando cache Redis |
| GET | `/api/alunos/matricula/{matricula}` | Busca por matricula usando LRU local |
| PUT | `/api/alunos/{id}` | Atualizacao de aluno |
| DELETE | `/api/alunos/{id}` | Remocao e invalidacao de cache |
| GET | `/api/disciplinas/autocompletar?q={q}` | Busca por prefixo via Trie |
| POST | `/api/desempenhos` | Registro de notas e calculo de media |
| GET | `/api/alunos/{id}/desempenho` | Consulta de desempenho |
| GET | `/api/ranking?top={k}` | Ranking Top-K por PriorityQueue |
| POST | `/api/relatorios` | Retorno 202 e publicacao no RabbitMQ |
| GET | `/api/admin/cache/stats` | Estatisticas do LRU e Trie |
| DELETE | `/api/admin/cache` | Limpeza de caches |
| GET | `/api/admin/rate-limiter/stats` | Estatisticas do rate limiter |

## 3. Testes Implementados

Testes obrigatorios ja iniciados:

- `LRUCacheTest`: capacidade, eviccao, acesso recente e thread-safety.
- `TrieTest`: prefixo, inexistente, limite e duplicidade.
- `RateLimiterTest`: limite, clientes independentes e expiracao da janela.
- `AlunoServiceTest`: cadastro, duplicidade e cache local por matricula.
- `DesempenhoServiceTest`: calculo de media final.
- `RankingServiceTest`: Top-K com maiores medias.
- `RelatorioProducerTest`: publicacao de evento RabbitMQ.
- `AlunoControllerTest`, `DisciplinaControllerTest`, `RelatorioControllerTest`: contratos HTTP principais.

Comandos:

```bash
mvn test
mvn package -DskipTests
```

## 4. Benchmark JMH

Classe criada:

- `src/main/java/br/edu/iftm/edumetrics/benchmark/EduMetricsBenchmark.java`

Benchmarks:

- `buscaLRUCache`
- `buscaHashMapDireto`
- `autocompletarTrie`
- `autocompletarLinear`
- `rateLimiterPermitir`

Comandos:

```bash
mvn -Pbenchmarks -DskipTests package
java -jar target/benchmarks.jar -rf json -rff jmh-resultado.json
```

O resultado deve ser usado para preencher a tabela JMH no README e discutir:

- Diferenca entre Trie e busca linear.
- Diferenca entre LRUCache e HashMap direto.
- Custo do RateLimiter com sincronizacao por cliente.
- Variancia dos resultados.

## 5. Roteiro Sugerido Para a Apresentacao

1. Apresentar o problema: API academica precisa consultar alunos, notas, ranking e relatorios com baixa latencia.
2. Mostrar arquitetura: Spring Boot, H2, Redis, RabbitMQ, JPA, services e controllers.
3. Explicar cada estrutura:
   - LRUCache para matricula.
   - Trie para disciplinas.
   - PriorityQueue para Top-K.
   - Sliding Window para rate limit.
4. Demonstrar endpoints:
   - Cadastrar aluno.
   - Buscar aluno por id.
   - Buscar por matricula duas vezes e mostrar stats do cache.
   - Autocompletar disciplina.
   - Registrar desempenho.
   - Consultar ranking.
   - Solicitar relatorio e ver log do consumer.
5. Mostrar testes passando com `mvn test`.
6. Mostrar resultados JMH e explicar o que os numeros indicam.
7. Apontar proximas melhorias.

## 6. Itens Que Ainda Devem Ser Preparados Para Entrega

- Rodar a aplicacao com `docker compose up -d` e validar Redis/RabbitMQ ao vivo (depende de Docker disponivel na maquina de apresentacao).
- Remover `target/` do versionamento, se ainda estiver commitado no GitHub (ver Secao 8).
- Criar slides com:
  - Arquitetura.
  - Estruturas de dados e complexidade.
  - Prints ou logs dos endpoints.
  - Tabela JMH.
  - Resultado JaCoCo.

## 7. Checklist Final Conforme PDF

- [x] Spring Boot compila (`mvn clean package -DskipTests`).
- [x] Docker Compose possui Redis e RabbitMQ.
- [x] Entidades JPA criadas.
- [x] DTOs criados com records.
- [x] LRUCache implementado manualmente.
- [x] Trie implementada manualmente.
- [x] RateLimiter implementado manualmente.
- [x] Controllers REST principais criados, incluindo `GET /api/admin/health`.
- [x] Services principais criados (inclui indexacao do nome do aluno na Trie ao cadastrar).
- [x] RabbitMQ producer/consumer criados, com validacao de tipo roteando para a DLQ.
- [x] Ranking com PriorityQueue criado.
- [x] Suite de testes completa (54 testes, `mvn test` 100% verde).
- [x] Cobertura JaCoCo >= 80% nos pacotes `domain` e `service` (na pratica, ~99%).
- [x] Benchmark JMH executado de fato; `jmh-resultado.json` gerado e commitado.
- [x] README com tabela JMH preenchida com numeros reais e analise qualitativa.
- [ ] Validar fluxo completo com Redis e RabbitMQ rodando via Docker (manual, no dia da apresentacao).
- [ ] Preparar apresentacao oral/slides.

## 8. Limpeza Pendente (decisao do usuario)

O diretorio `target/` tem arquivos de build versionados desde o commit inicial (antes do `.gitignore` existir). Sao artefatos binarios (`.class`, `.jar`) que nao deveriam estar no Git. Para remover do rastreamento sem apagar os arquivos locais:

```bash
git rm -r --cached target
git commit -m "chore: remove target/ do versionamento"
```
