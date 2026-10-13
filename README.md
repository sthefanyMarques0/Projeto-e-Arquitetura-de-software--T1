# Sistema de Gestão e Validação de Planos de Voo (Trabalho Final PAS)

Backend RESTful desenvolvido em **Java 17** e **Spring Boot 3** para criação, validação progressiva, aprovação com reserva integral e gerenciamento do ciclo de vida de planos de voo.

---

## 1. Data de Referência e Carga Inicial Obrigatória (Seção 8.2)

* **Data de Referência Oficial:** `2026-11-20`
* **Malha Aérea Inicial:**
  * **Aeroportos:** POA (41 vagas), FLN (16 vagas), CWB (26 vagas), GRU (115 vagas).
  * **Aerovias (R1 a R8):**
    * R1: POA -> FLN (363 km)
    * R2: POA -> GRU (865 km)
    * R3: GRU -> POA (865 km)
    * R4: FLN -> CWB (245 km)
    * R5: CWB -> FLN (245 km)
    * R6: CWB -> GRU (360 km)
    * R7: GRU -> CWB (360 km)
    * R8: FLN -> POA (363 km)
* **Ocupação Inicial da Malha (>= 60%):**
  * Na data de referência `2026-11-20`, foram pré-carregados e aprovados **4 planos de voo**, gerando reservas que ocupam **5 das 8 aerovias iniciais (62,5%)**:
    1. **Plano 1 (Comercial Passageiros PT-AZU):** POA -> FLN (R1) -> CWB (R4), altitude 29.000 pés, partida 08:00. Ocupa **R1** e **R4**.
    2. **Plano 2 (Comercial Passageiros PP-GLO):** CWB -> GRU (R6), altitude 31.000 pés, partida 10:00. Ocupa **R6**.
    3. **Plano 3 (Comercial Carga PR-CRG):** GRU -> POA (R3), altitude 28.000 pés, partida 02:00 (janela de carga 00:00-06:00). Ocupa **R3**.
    4. **Plano 4 (Particular PR-EXE):** CWB -> FLN (R5), altitude 26.000 pés, partida 14:00. Ocupa **R5**.
  * **Aerovias com ocupação:** R1, R3, R4, R5, R6.
  * **Aerovias livres para testes de criação:** R2, R7, R8 (e faixas horárias livres nas demais).

---

## 2. Arquitetura da Solução e Modelo C4 (Seção 7.4)

### C4 Nível 1: Diagrama de Contexto
```
+-------------------------------------------------------------+
|                     Usuário / Operador                      |
|           (Piloto, Despachante ou Administrador)            |
+-------------------------------------------------------------+
                              |
                              | Requisições HTTP REST (JSON)
                              v
+-------------------------------------------------------------+
|        Sistema de Gestão de Planos de Voo (Backend)         |
|  Responsável por gerenciar rede aérea, aeronaves, pilotos,  |
|  validar percursos e controlar a ocupação em slots horários |
+-------------------------------------------------------------+
```

### C4 Nível 2: Diagrama de Contêineres
```
+-------------------------------------------------------------------+
|               Contêiner 1: API REST (Spring Boot 3)               |
|  - Web MVC: Controllers REST para UC01 a UC20                     |
|  - Camada de Serviço: Algoritmos de validação, cálculo e reservas |
|  - Spring Data JPA: Repositórios e Data Mappers                   |
|  - OpenAPI 3 / Swagger UI integrado                               |
+-------------------------------------------------------------------+
                                  |
                                  | Conexão JDBC / SQL
                                  v
+-------------------------------------------------------------------+
|            Contêiner 2: Banco de Dados Relacional H2              |
|  - Tabelas: aeroportos, aerovias, aeronaves, pilotos,            |
|             planos_de_voo, trechos, reservas                     |
+-------------------------------------------------------------------+
```

### C4 Nível 3: Diagrama de Componentes
* **Controladores (`controller`):**
  * `RedeAereaController`: UC01, UC02, UC03, UC04.
  * `PilotoAeronaveController`: UC05, UC06, UC07, UC08.
  * `DisponibilidadeController`: UC13, UC14.
  * `PlanoDeVooController`: UC09 a UC12, UC15 a UC20.
* **Serviços de Domínio e Aplicação (`servico`):**
  * `ServicoCalculoHorarios`: projeção de chegada a partir da velocidade e distância.
  * `ServicoAutonomia`: controle de 85% da autonomia nominal entre pousos.
  * `ServicoDisponibilidade`: verificação de slots de hora cheia livres.
  * `ServicoValidacaoPlano`: validação sem reservas das 12 regras do regulamento.
  * `ServicoAprovacaoPlano`: controle transacional (`@Transactional`) de reservas "tudo-ou-nada", cancelamento futuro e abandono.
* **Repositórios (`repositorio`):**
  * Interfaces Spring Data JPA para cada entidade.

---

## 3. Instruções de Compilação e Execução

### Pré-requisitos
* Java 17+ instalado
* Maven 3.8+ instalado

### Passos
1. Abra o terminal na pasta raiz do projeto.
2. Compile e execute:
   ```bash
   mvn spring-boot:run
   ```
3. A aplicação estará ativa em `http://localhost:8080`.
4. Acesse a documentação interativa OpenAPI / Swagger UI:
   `http://localhost:8080/swagger-ui.html`
5. Acesse o console do banco H2:
   `http://localhost:8080/h2-console`
   * JDBC URL: `jdbc:h2:mem:planosdb`
   * User Name: `sa`
   * Password: *(em branco)*
