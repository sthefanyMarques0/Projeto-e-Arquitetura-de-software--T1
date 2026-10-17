-- Planos iniciais e reservas, gerados com as próprias regras do domínio
-- (duração = ceil(distância / velocidade × 3600 s); slots de 1 h em [início, fim)).
--
-- DATA DE REFERÊNCIA: 2026-11-20
-- Aerovias com ocupação nessa data: R1, R3, R4, R5, R6 (5 de 8 = 62,5%).
-- Livres na data: R2, R7, R8 e as demais altitudes/horários das aerovias ocupadas.
--
-- Planos históricos são carregados aqui (e não pela API), pois a API só aprova partidas futuras.

-- Plano 1 (APROVADO): PT-AZU POA -> CWB, partida 2026-11-20T08:00
INSERT INTO planos_de_voo (id, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida_prevista, situacao) VALUES (1, 101, 'PT-AZU', 'POA', 'CWB', TIMESTAMP '2026-11-20 08:00:00', 'APROVADO');
INSERT INTO trechos (plano_id, ordem, aerovia_id, altitude, entrada_prevista, chegada_prevista, tipo_passagem, permanencia_minutos, vaga_pretendida) VALUES (1, 0, 'R1', 29000, TIMESTAMP '2026-11-20 08:00:00', TIMESTAMP '2026-11-20 08:25:38', 'SOBREVOO', NULL, NULL);
INSERT INTO trechos (plano_id, ordem, aerovia_id, altitude, entrada_prevista, chegada_prevista, tipo_passagem, permanencia_minutos, vaga_pretendida) VALUES (1, 1, 'R4', 29000, TIMESTAMP '2026-11-20 08:25:38', TIMESTAMP '2026-11-20 08:42:56', 'POUSO', 60, 1);
INSERT INTO reservas (plano_id, tipo, aerovia_id, altitude, inicio_slot) VALUES (1, 'ALTITUDE_AEROVIA', 'R1', 29000, TIMESTAMP '2026-11-20 08:00:00');
INSERT INTO reservas (plano_id, tipo, aerovia_id, altitude, inicio_slot) VALUES (1, 'ALTITUDE_AEROVIA', 'R4', 29000, TIMESTAMP '2026-11-20 08:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (1, 'VAGA_AEROPORTO', 'CWB', 1, TIMESTAMP '2026-11-20 08:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (1, 'VAGA_AEROPORTO', 'CWB', 1, TIMESTAMP '2026-11-20 09:00:00');

-- Plano 2 (APROVADO): PP-GLO CWB -> GRU, partida 2026-11-20T10:00
INSERT INTO planos_de_voo (id, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida_prevista, situacao) VALUES (2, 102, 'PP-GLO', 'CWB', 'GRU', TIMESTAMP '2026-11-20 10:00:00', 'APROVADO');
INSERT INTO trechos (plano_id, ordem, aerovia_id, altitude, entrada_prevista, chegada_prevista, tipo_passagem, permanencia_minutos, vaga_pretendida) VALUES (2, 0, 'R6', 31000, TIMESTAMP '2026-11-20 10:00:00', TIMESTAMP '2026-11-20 10:25:25', 'POUSO', 120, 2);
INSERT INTO reservas (plano_id, tipo, aerovia_id, altitude, inicio_slot) VALUES (2, 'ALTITUDE_AEROVIA', 'R6', 31000, TIMESTAMP '2026-11-20 10:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (2, 'VAGA_AEROPORTO', 'GRU', 2, TIMESTAMP '2026-11-20 10:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (2, 'VAGA_AEROPORTO', 'GRU', 2, TIMESTAMP '2026-11-20 11:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (2, 'VAGA_AEROPORTO', 'GRU', 2, TIMESTAMP '2026-11-20 12:00:00');

-- Plano 3 (APROVADO): PR-CRG GRU -> POA, partida 2026-11-20T02:00
INSERT INTO planos_de_voo (id, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida_prevista, situacao) VALUES (3, 103, 'PR-CRG', 'GRU', 'POA', TIMESTAMP '2026-11-20 02:00:00', 'APROVADO');
INSERT INTO trechos (plano_id, ordem, aerovia_id, altitude, entrada_prevista, chegada_prevista, tipo_passagem, permanencia_minutos, vaga_pretendida) VALUES (3, 0, 'R3', 28000, TIMESTAMP '2026-11-20 02:00:00', TIMESTAMP '2026-11-20 03:09:12', 'POUSO', 90, 5);
INSERT INTO reservas (plano_id, tipo, aerovia_id, altitude, inicio_slot) VALUES (3, 'ALTITUDE_AEROVIA', 'R3', 28000, TIMESTAMP '2026-11-20 02:00:00');
INSERT INTO reservas (plano_id, tipo, aerovia_id, altitude, inicio_slot) VALUES (3, 'ALTITUDE_AEROVIA', 'R3', 28000, TIMESTAMP '2026-11-20 03:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (3, 'VAGA_AEROPORTO', 'POA', 5, TIMESTAMP '2026-11-20 03:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (3, 'VAGA_AEROPORTO', 'POA', 5, TIMESTAMP '2026-11-20 04:00:00');

-- Plano 4 (APROVADO): PR-EXE CWB -> FLN, partida 2026-11-20T14:00
INSERT INTO planos_de_voo (id, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida_prevista, situacao) VALUES (4, 104, 'PR-EXE', 'CWB', 'FLN', TIMESTAMP '2026-11-20 14:00:00', 'APROVADO');
INSERT INTO trechos (plano_id, ordem, aerovia_id, altitude, entrada_prevista, chegada_prevista, tipo_passagem, permanencia_minutos, vaga_pretendida) VALUES (4, 0, 'R5', 26000, TIMESTAMP '2026-11-20 14:00:00', TIMESTAMP '2026-11-20 14:24:30', 'POUSO', 45, 3);
INSERT INTO reservas (plano_id, tipo, aerovia_id, altitude, inicio_slot) VALUES (4, 'ALTITUDE_AEROVIA', 'R5', 26000, TIMESTAMP '2026-11-20 14:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (4, 'VAGA_AEROPORTO', 'FLN', 3, TIMESTAMP '2026-11-20 14:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (4, 'VAGA_AEROPORTO', 'FLN', 3, TIMESTAMP '2026-11-20 15:00:00');

-- Plano 5 (APROVADO, partida já ocorrida: voo executado, NÃO pode ser cancelado): PT-AZU POA -> GRU, partida 2026-10-01T09:00
INSERT INTO planos_de_voo (id, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida_prevista, situacao) VALUES (5, 101, 'PT-AZU', 'POA', 'GRU', TIMESTAMP '2026-10-01 09:00:00', 'APROVADO');
INSERT INTO trechos (plano_id, ordem, aerovia_id, altitude, entrada_prevista, chegada_prevista, tipo_passagem, permanencia_minutos, vaga_pretendida) VALUES (5, 0, 'R2', 30000, TIMESTAMP '2026-10-01 09:00:00', TIMESTAMP '2026-10-01 10:01:04', 'POUSO', 60, 4);
INSERT INTO reservas (plano_id, tipo, aerovia_id, altitude, inicio_slot) VALUES (5, 'ALTITUDE_AEROVIA', 'R2', 30000, TIMESTAMP '2026-10-01 09:00:00');
INSERT INTO reservas (plano_id, tipo, aerovia_id, altitude, inicio_slot) VALUES (5, 'ALTITUDE_AEROVIA', 'R2', 30000, TIMESTAMP '2026-10-01 10:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (5, 'VAGA_AEROPORTO', 'GRU', 4, TIMESTAMP '2026-10-01 10:00:00');
INSERT INTO reservas (plano_id, tipo, aeroporto_codigo, vaga, inicio_slot) VALUES (5, 'VAGA_AEROPORTO', 'GRU', 4, TIMESTAMP '2026-10-01 11:00:00');

-- Plano 6 (EM_EDICAO, para demonstrar CONFLITO: usa R1 a 29000 pés no mesmo slot do plano 1): PT-AZU POA -> FLN, partida 2026-11-20T08:10
INSERT INTO planos_de_voo (id, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida_prevista, situacao) VALUES (6, 102, 'PT-AZU', 'POA', 'FLN', TIMESTAMP '2026-11-20 08:10:00', 'EM_EDICAO');
INSERT INTO trechos (plano_id, ordem, aerovia_id, altitude, entrada_prevista, chegada_prevista, tipo_passagem, permanencia_minutos, vaga_pretendida) VALUES (6, 0, 'R1', 29000, TIMESTAMP '2026-11-20 08:10:00', TIMESTAMP '2026-11-20 08:35:38', 'POUSO', 40, 2);

-- Plano 7 (EM_EDICAO, válido e pronto para demonstrar aprovação e depois cancelamento): PP-GLO GRU -> CWB, partida 2026-11-20T16:00
INSERT INTO planos_de_voo (id, piloto_id, aeronave_prefixo, origem_codigo, destino_codigo, partida_prevista, situacao) VALUES (7, 104, 'PP-GLO', 'GRU', 'CWB', TIMESTAMP '2026-11-20 16:00:00', 'EM_EDICAO');
INSERT INTO trechos (plano_id, ordem, aerovia_id, altitude, entrada_prevista, chegada_prevista, tipo_passagem, permanencia_minutos, vaga_pretendida) VALUES (7, 0, 'R7', 30000, TIMESTAMP '2026-11-20 16:00:00', TIMESTAMP '2026-11-20 16:25:25', 'POUSO', 30, 10);

-- Os próximos planos e reservas criados pela API recebem ids a partir de 100.
ALTER TABLE planos_de_voo ALTER COLUMN id RESTART WITH 100;
ALTER TABLE reservas ALTER COLUMN id RESTART WITH 1000;
