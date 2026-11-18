# Microsserviços da Parte 2

## Executar o ambiente completo

Requisitos: Docker Desktop com Docker Compose v2 e Java 25 se os serviços forem executados fora dos contêineres.

```powershell
docker compose up --build
```

O gateway público da demonstração é `http://localhost:8090`. Para desligar sem apagar os dados persistentes:

```powershell
docker compose down
```

Não use `docker compose down -v` salvo se quiser apagar explicitamente os bancos e a fila. O Compose mantém volumes separados por serviço; o Flyway carrega a rede uma única vez. A carga inicial de planos continua no backend de gestão.

| Componente | Endereço/porta no host | Uso |
|---|---:|---|
| API Gateway | `localhost:8090` | Ponto de entrada HTTP recomendado |
| Gestão de planos (direto) | `localhost:8081` | Porta publicada para diagnóstico |
| Cadastro da rede (direto) | `localhost:8082` | API independente e cliente HTTP |
| Registro de aprovações (direto) | `localhost:8083` | Consulta de histórico |
| Eureka | `localhost:8761` | Interface do catálogo de serviços |
| RabbitMQ | `localhost:5672` | AMQP |
| RabbitMQ Management | `localhost:15672` | Diagnóstico; credenciais locais padrão `guest`/`guest` |

O gateway é configurado com rotas explícitas `lb://planos-de-voo`, `lb://cadastro-rede` e `lb://registro-aprovacoes`. As aplicações de negócio registram-se no Eureka; o gateway e a gestão usam a descoberta para encaminhamento e consultas. As portas diretas são publicadas apenas para diagnóstico/demonstração; clientes externos devem usar o gateway.

## Contratos HTTP da rede

O serviço `cadastro-rede` mantém seu próprio H2 em `./data/cadastro-rede` e carrega POA, FLN, CWB, GRU e as aerovias R1–R8 via Flyway. Os registros iniciais não são atualizados em reinicializações.

| Método | Rota | Resposta |
|---|---|---|
| GET | `/api/aeroportos` | Lista `{codigo, nome, vagas}` |
| GET | `/api/aeroportos/{codigo}` | Aeroporto ou 404 |
| GET | `/api/aeroportos/{codigo}/vagas` | `{aeroportoCodigo, quantidade, numeros}` ou 404 |
| POST | `/api/aeroportos` | 201; código repetido retorna 409 |
| GET | `/api/aerovias?origem=&destino=` | Lista/filtro por origem e/ou destino |
| GET | `/api/aerovias/{id}` | Aerovia ou 404 |
| POST | `/api/aerovias` | 201; aeroportos devem existir; ID repetido retorna 409 |

O contrato de resposta de aerovia mantém os códigos de origem e destino. A integração da gestão completa a referência do domínio consultando os aeroportos pela mesma porta HTTP/load-balancer. A integração usa timeout de conexão de 2 s e leitura de 3 s. 404 significa referência ausente; timeout, indisponibilidade, erro de protocolo ou erro 5xx resultam em 503 na API de gestão. A aprovação falha fechada se não conseguir validar as referências.

As tabelas locais antigas de aeroportos/aerovias na gestão são limpas e desvinculadas das tabelas de planos, trechos e reservas pela migração Java V7. Permanecem somente como estrutura legada vazia para compatibilidade do mapeamento. A V4/V5 ainda carrega os planos de demonstração antes da V7; a partir daí, seus códigos são resolvidos exclusivamente pela API de cadastro. Reservas continuam locais e identificam aeroporto/vaga por código e número, sem FK entre bancos.

## Evento de aprovação

Exchange `aprovacoes.exchange` (topic, durável), routing key `aprovacao.confirmada`, fila `aprovacoes.queue` (durável), mensagens persistentes. O corpo JSON tem `versaoContrato: "1.0"`, `eventoId`, `planoId`, `aprovadoEm`, `criadoPor`, piloto, aeronave com características relevantes, origem/destino, partida e trechos ordenados (aerovia, altitude, entrada, chegada calculada, passagem, vaga/permanência).

`POST /api/planos/{id}/aprovacao` grava reservas, estado aprovado e o snapshot serializado na outbox dentro da mesma transação local. O agendador publica os registros pendentes; só depois de publisher confirm do broker marca `publicado_em`. Falha do broker mantém a outbox pendente e produz log com IDs de evento e plano. Se o processo cair entre a confirmação do broker e a atualização da outbox, a entrega pode duplicar; o consumidor verifica `eventoId` e `planoId` únicos, portanto a reentrega não cria outro histórico.

O listener confirma a mensagem somente quando a transação de persistência no banco do registro termina. Erros de JSON ou banco são lançados e reentregues; acompanhe logs do consumidor e a fila no RabbitMQ Management. O armazenamento de histórico contém o JSON recebido integralmente: consultas não precisam contatar a gestão ou cadastro. Cancelar remove reservas operacionais, mas não apaga o snapshot.

As aprovações preexistentes da carga inicial são preparadas na outbox com UUID estável (`plano-aprovado-legado-v1:<id>`); o job repete a reconciliação sem duplicar registros. Uma aprovação confirmada pelo gateway não garante ainda a persistência no consumidor: a consulta histórica é eventualmente consistente.

## Criador e identidade

O `POST /api/planos` exige `criadoPor`, persistido como metadado imutável e incluído no evento de aprovação. A Parte 1 não adotava autenticação nem provedor de identidade, e a Parte 2 não exige desenvolver um; portanto o valor é declarado pelo cliente e não constitui prova de identidade nem controle de acesso autenticado.

## Sequência recomendada para demonstrar

1. Abrir `http://localhost:8761` e confirmar os três serviços de negócio e gateway no Eureka.
2. Consultar a rede pelo gateway: `GET http://localhost:8090/api/aeroportos` e `GET http://localhost:8090/api/aerovias?origem=POA`.
3. Criar/validar plano no gateway, com `criadoPor`; validar não gera outbox.
4. Aprovar, verificar reservas em `/api/planos/{id}/reservas` e esperar o consumo em `/api/aprovacoes/{id}`.
5. Parar temporariamente `registro-aprovacoes` ou `rabbitmq`, aprovar um plano diferente e confirmar aprovação local + outbox pendente. Reiniciar o componente e acompanhar o evento até o registro.
6. Confirmar que o mesmo evento não cria uma segunda linha e que cancelamento libera reservas sem remover o histórico.
