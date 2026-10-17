# API REST — operações, parâmetros, requisições e respostas

Documentação da API (equivalente ao OpenAPI, conforme seção 8.5 do enunciado).
Base: `http://localhost:8080`. Datas no formato `AAAA-MM-DDTHH:mm:ss`, sem fuso, interpretadas em America/Sao_Paulo.

O roteiro completo de demonstração está em [`planos-de-voo.postman_collection.json`](planos-de-voo.postman_collection.json) (Postman → Import).

## Rede aérea, pilotos e aeronaves (somente consulta)

Esses dados vêm da carga inicial (migrações Flyway V2 e V3); não há endpoints de cadastro.

| Método | Rota | Caso de uso | Resposta |
|---|---|---|---|
| GET | `/api/aeroportos` | UC01 | `200` lista de `{codigo, nome, vagas}` |
| GET | `/api/aerovias` | UC02 | `200` lista de `{id, origem, destino, distanciaKm}` |
| GET | `/api/pilotos` | UC03 | `200` lista de `{id, nome}` |
| GET | `/api/aeronaves` | UC04 | `200` lista com `prefixo, categoria, velocidadeCruzeiroKmH, autonomiaNominalKm` e os campos da categoria |

## Disponibilidade (não reserva nada)

| Método | Rota | Caso de uso |
|---|---|---|
| GET | `/api/disponibilidade/aerovias/{aeroviaId}/altitudes?inicio=&fim=[&aeronave=]` | UC09 |
| GET | `/api/disponibilidade/aeroportos/{codigo}/vagas?inicio=&fim=` | UC10 |

Altitudes: devolve as altitudes de 25.000 a 35.000 pés livres em **todos** os slots do período. Com `aeronave=<prefixo>`, aplica também as regras da categoria.

```json
{ "aeroviaId": "R1", "inicio": "2026-11-20T08:00:00", "fim": "2026-11-20T09:00:00",
  "altitudesDisponiveis": [25000, 26000, 27000, 28000, 30000, 31000, 32000, 33000, 34000, 35000] }
```

Vagas: `{ "aeroportoCodigo": "CWB", "inicio": ..., "fim": ..., "quantidadeDisponivel": 25, "vagasDisponiveis": [2, 3, ...] }`

## Planos de voo

| Método | Rota | Caso de uso | Sucesso | Erros |
|---|---|---|---|---|
| POST | `/api/planos` | UC05 Criar | `201` + `Location` | 400, 404 |
| PUT | `/api/planos/{id}` | UC06 Editar dados | `200` | 400, 404, 409 |
| PUT | `/api/planos/{id}/percurso` | UC07/UC08 Definir percurso e trechos | `200` | 400, 404, 409 |
| POST | `/api/planos/{id}/validacao` | UC11 Validar | `200` `{valido, erros}` | 404, 409 |
| POST | `/api/planos/{id}/aprovacao` | UC12 Aprovar | `200` | 404, 409, 422 |
| GET | `/api/planos/{id}` | UC13 Consultar | `200` | 404 |
| GET | `/api/planos[?situacao=]` | UC14 Listar por situação | `200` | 400 |
| GET | `/api/planos/{id}/reservas` | Reservas do plano | `200` | 404 |
| POST | `/api/planos/{id}/abandono` | UC15 Abandonar | `200` | 404, 409 |
| POST | `/api/planos/{id}/cancelamento` | UC16 Cancelar | `200` | 404, 409 |

`situacao`: `EM_EDICAO`, `APROVADO`, `CANCELADO`, `ABANDONADO`.

### Criar ou editar (PlanoRequest)

```json
{ "pilotoId": 103, "prefixoAeronave": "PT-AZU", "origem": "POA", "destino": "CWB",
  "partidaPrevista": "2026-11-20T11:00:00" }
```

Ao trocar a aeronave na edição, as chegadas dos trechos são recalculadas.

### Definir percurso (PercursoRequest)

Substitui o percurso inteiro (adicionar, remover e reordenar = enviar a nova lista). A chegada **não** é enviada: o sistema calcula `entrada + ceil(distância / velocidade × 3600 s)`.

```json
{ "trechos": [
  { "aeroviaId": "R1", "altitude": 30000, "entradaPrevista": "2026-11-20T11:00:00", "tipoPassagem": "SOBREVOO" },
  { "aeroviaId": "R4", "altitude": 30000, "entradaPrevista": "2026-11-20T11:25:38", "tipoPassagem": "POUSO",
    "permanenciaMinutos": 30, "vagaPretendida": 7 }
] }
```

Em `POUSO`, `permanenciaMinutos` (≥ 1) e `vagaPretendida` são obrigatórios; em `SOBREVOO`, não devem ser enviados (400).

### Resposta de plano (PlanoResponse)

```json
{ "id": 100, "situacao": "EM_EDICAO", "pilotoId": 103, "pilotoNome": "Cmte. Daniel Oliveira",
  "prefixoAeronave": "PT-AZU", "categoriaAeronave": "COMERCIAL_PASSAGEIROS",
  "origem": "POA", "destino": "CWB", "partidaPrevista": "2026-11-20T11:00:00",
  "chegadaPrevistaDestino": "2026-11-20T11:42:56",
  "trechos": [ { "ordem": 1, "aeroviaId": "R1", "origem": "POA", "destino": "FLN", "distanciaKm": 363,
                 "altitude": 30000, "entradaPrevista": "2026-11-20T11:00:00",
                 "chegadaPrevista": "2026-11-20T11:25:38", "duracaoSegundos": 1538, "tipoPassagem": "SOBREVOO" } ] }
```

### Validação

`200` sempre que o plano existir e estiver em edição — o corpo diz se é válido:

```json
{ "planoId": 6, "valido": false,
  "erros": ["Trecho 1 (R1): aerovia R1 a 29000 pés indisponível nos slots [2026-11-20 08:00–09:00] (reservado por outro plano aprovado)."] }
```

### Aprovação

Exige partida estritamente futura. Revalida, grava **todas** as reservas e muda para `APROVADO` numa única transação. Se o plano for inválido, responde `422` com a lista de erros; se outro plano reservar o recurso no meio do caminho, `409`. Em ambos os casos nenhuma reserva permanece e o plano continua `EM_EDICAO`.

### Reservas do plano

```json
[ { "id": 1, "planoId": 1, "tipo": "ALTITUDE_AEROVIA", "aeroviaId": "R1", "altitude": 29000,
    "inicioSlot": "2026-11-20T08:00:00", "fimSlot": "2026-11-20T09:00:00" },
  { "id": 3, "planoId": 1, "tipo": "VAGA_AEROPORTO", "aeroportoCodigo": "CWB", "vaga": 1,
    "inicioSlot": "2026-11-20T08:00:00", "fimSlot": "2026-11-20T09:00:00" } ]
```

## Erros

Corpo padronizado: `{ "status": 409, "erro": "Conflict", "mensagem": "...", "detalhes": [...] }`

| Código | Quando |
|---|---|
| 400 | JSON ilegível, data fora do formato, campo obrigatório ausente, pouso sem permanência, período com início ≥ fim |
| 404 | Plano, piloto, aeronave, aeroporto ou aerovia inexistente |
| 409 | Operação não permitida na situação atual (alterar/validar/aprovar plano que não está em edição, cancelar plano não aprovado ou já executado, aprovar com partida passada) ou conflito de reserva concorrente |
| 422 | Aprovação recusada porque o plano não passa na validação (`detalhes` traz os erros) |
