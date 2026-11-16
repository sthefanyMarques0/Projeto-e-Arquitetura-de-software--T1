# Sistema de Gestão de Planos de Voo — Trabalho Final PAS

Backend REST em **Java 25 + Spring Boot 4.1** com **arquitetura hexagonal** (portas e adaptadores), organizado como o exemplo `ex7` da disciplina.

> **Data de referência: `2027-01-15`** — usada nas demonstrações de validação, disponibilidade, reserva e conflito.
> Ela fica no futuro durante todo o semestre, então aprovação e cancelamento funcionam com o relógio real.

## Pré-requisitos

- Java 25 (JDK), com `JAVA_HOME` apontando para ele (`java -version` deve mostrar 25).
- Não é preciso instalar Maven nem banco: o projeto traz o Maven Wrapper e usa H2 embutido. Na primeira execução o wrapper baixa o Maven e as dependências (precisa de internet).

## Executar

Na raiz do projeto:

```bash
./mvnw spring-boot:run          # Linux/macOS
mvnw.cmd spring-boot:run        # Windows
```

A API sobe em `http://localhost:8080`. Ao iniciar pela primeira vez, o **Flyway** cria o banco em `./data/planos-de-voo.mv.db` e carrega os dados iniciais. As alterações feitas pela API ficam gravadas nesse arquivo e persistem após reiniciar.

- **Recomeçar do zero (dados iniciais):** pare a aplicação e apague a pasta `./data`.
- **Console do banco:** `http://localhost:8080/h2-console` — JDBC URL `jdbc:h2:file:./data/planos-de-voo`, usuário `sa`, senha vazia.

## Testar

```bash
./mvnw clean test
```

| Testes | O que verificam |
|---|---|
| `domain/*Test` | Regras do domínio sem Spring nem banco: duração, slots, categorias, autonomia, percurso, horários, estados do plano |
| `PlanosDeVooIntegracaoTest` | Carga inicial, validar sem reservar, aprovar e cancelar, conflito, voo executado, disponibilidade no momento da aprovação, aprovação sem reservas parciais |
| `CadastrosIntegracaoTest` | Cadastro de aeroportos, aerovias (imutáveis), pilotos e aeronaves de cada categoria |
| `RoteiroDemonstracaoTest` | O roteiro da apresentação (coleção Postman): cada falha de validação aparece como esperado na data de referência |

Os testes de integração sobem a aplicação com H2 em memória, as mesmas migrações (V1–V5) e o relógio fixo em `2027-01-14T12:00:00`.

## Acessar a API

- **Especificação OpenAPI 3:** [`docs/api/openapi.yaml`](docs/api/openapi.yaml). Para navegar e testar, abra em [editor.swagger.io](https://editor.swagger.io) (File → Import file) ou importe no Postman.
- Guia das operações com exemplos: [`docs/api/endpoints.md`](docs/api/endpoints.md)
- Roteiro da apresentação no Postman: importe [`docs/api/planos-de-voo.postman_collection.json`](docs/api/planos-de-voo.postman_collection.json) e rode as pastas na ordem (1 a 8).

```bash
curl http://localhost:8080/api/aerovias
curl -X POST http://localhost:8080/api/planos/7/validacao
```

## Dados iniciais (migrações em `src/main/resources/db/migration`)

| Migração | Conteúdo |
|---|---|
| `V1__criar_esquema.sql` | Tabelas, restrições e unicidade das reservas por slot |
| `V2__carregar_rede_aerea.sql` | POA, FLN, CWB, GRU e aerovias R1–R8 |
| `V3__carregar_pilotos_e_aeronaves.sql` | 4 pilotos; aeronaves das 3 categorias (PT-ECO com pouca autonomia, para demonstração) |
| `V4__carregar_planos_e_reservas.sql` | Planos e reservas (gerados com as regras do domínio) |
| `V5__mover_data_de_referencia.sql` | Move a data de referência de 2026-11-20 para 2027-01-15 |

**Ocupação em 2027-01-15:** aerovias **R1, R3, R4, R5 e R6** (5 de 8 = 62,5%). R2, R7 e R8 estão livres, assim como as demais altitudes e horários.

| Plano | Situação | Percurso | Uso na demonstração |
|---|---|---|---|
| 1 | APROVADO | PT-AZU POA→R1→FLN→R4→CWB, 08:00, 29.000 pés, vaga 1 de CWB | Ocupação e conflito |
| 2 | APROVADO | PP-GLO CWB→R6→GRU, 10:00, 31.000 pés | Ocupação |
| 3 | APROVADO | PR-CRG (carga) GRU→R3→POA, 02:00, 28.000 pés | Janela de carga |
| 4 | APROVADO | PR-EXE (particular) CWB→R5→FLN, 14:00, 26.000 pés | Ocupação |
| 5 | APROVADO | PT-AZU POA→R2→GRU em **2026-10-01** | Voo já executado: não pode ser cancelado |
| 6 | EM_EDICAO | PT-AZU POA→R1→FLN, 08:10, 29.000 pés | Conflito com o plano 1 (validação e aprovação falham) |
| 7 | EM_EDICAO | PP-GLO GRU→R7→CWB, 16:00, 30.000 pés | Válido: aprovar e depois cancelar |

Planos criados pela API recebem ids a partir de 100.

Aeroportos, aerovias, pilotos e aeronaves vêm da carga inicial e também podem ser cadastrados pela API (`POST /api/aeroportos`, `/api/aerovias`, `/api/pilotos`, `/api/aeronaves`). Identificador repetido é recusado com `409`: aerovias nunca são alteradas ou excluídas. Os cadastros feitos pela API persistem em `./data`; para voltar aos dados iniciais, apague essa pasta.

## Relógio da aplicação

A hora atual é obtida no fuso **America/Sao_Paulo**, independentemente do fuso do servidor. Para demonstrar situações dependentes do horário, é possível fixar o "agora" em `src/main/resources/application.properties`:

```properties
planos.relogio.agora=2027-01-15T09:00:00
```

Com esse valor, por exemplo, o plano 1 (partida às 08:00) passa a ser um voo executado e não pode ser cancelado.

## Documentação

- Arquitetura e diagramas C4: [`docs/arquitetura/README.md`](docs/arquitetura/README.md)
- Decisões e alternativas: [`docs/arquitetura/decisoes/decisoes.md`](docs/arquitetura/decisoes/decisoes.md)
- Modelo de domínio (UML): [`docs/dominio/README.md`](docs/dominio/README.md)
- Enunciado: [`PAS_TF_2026_2_PlanosDeVoo.md`](PAS_TF_2026_2_PlanosDeVoo.md)
