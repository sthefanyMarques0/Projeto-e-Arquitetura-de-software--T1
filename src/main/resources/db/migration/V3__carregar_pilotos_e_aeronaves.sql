-- Pilotos e aeronaves das três categorias.
-- PT-ECO tem autonomia baixa de propósito, para demonstrar "autonomia insuficiente".

INSERT INTO pilotos (id, nome) VALUES
    (101, 'Cmte. Carlos Silva'),
    (102, 'Cmte. Beatriz Santos'),
    (103, 'Cmte. Daniel Oliveira'),
    (104, 'Cmte. Mariana Souza');

INSERT INTO aeronaves (prefixo, categoria, velocidade_cruzeiro_km_h,
    autonomia_nominal_km, empresa_manutencao, companhia_aerea,
    capacidade_maxima_passageiros, capacidade_maxima_toneladas)
VALUES
    ('PR-EXE', 'PARTICULAR', 600, 2200, 'Helisul Aviação', NULL, NULL, NULL),
    ('PT-ECO', 'PARTICULAR', 280, 500, 'Aeroclube RS Manutenção', NULL, NULL, NULL),
    ('PT-AZU', 'COMERCIAL_PASSAGEIROS', 850, 4200, NULL, 'Azul Linhas Aéreas', 174, NULL),
    ('PP-GLO', 'COMERCIAL_PASSAGEIROS', 850, 4500, NULL, 'Gol Transportes Aéreos', 186, NULL),
    ('PR-CRG', 'COMERCIAL_CARGA', 750, 5000, NULL, 'Total Cargo', NULL, 35);
