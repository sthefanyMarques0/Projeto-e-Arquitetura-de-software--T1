-- Move a DATA DE REFERÊNCIA de 2026-11-20 para 2027-01-15 (+56 dias).
--
-- Motivo: com a data antiga, depois de 20/11/2026 todos os planos da data de referência
-- passariam a ser voos "já executados" e a apresentação não conseguiria demonstrar
-- aprovação e cancelamento com o relógio real. A nova data fica no futuro durante
-- todo o semestre.
--
-- É uma migração nova (e não uma edição da V4) para que bancos já criados com a
-- versão anterior continuem iniciando: o Flyway recusa migrações aplicadas e depois
-- alteradas. O plano 5 (2026-10-01, voo já executado) não é afetado.
--
-- Depois desta migração, a ocupação na data de referência continua a mesma:
-- aerovias R1, R3, R4, R5 e R6 (5 de 8 = 62,5%).

UPDATE trechos
   SET entrada_prevista = DATEADD('DAY', 56, entrada_prevista),
       chegada_prevista = DATEADD('DAY', 56, chegada_prevista)
 WHERE plano_id IN (SELECT id FROM planos_de_voo
                     WHERE id < 100 AND CAST(partida_prevista AS DATE) = DATE '2026-11-20');

UPDATE reservas
   SET inicio_slot = DATEADD('DAY', 56, inicio_slot)
 WHERE plano_id IN (SELECT id FROM planos_de_voo
                     WHERE id < 100 AND CAST(partida_prevista AS DATE) = DATE '2026-11-20');

UPDATE planos_de_voo
   SET partida_prevista = DATEADD('DAY', 56, partida_prevista)
 WHERE id < 100 AND CAST(partida_prevista AS DATE) = DATE '2026-11-20';
