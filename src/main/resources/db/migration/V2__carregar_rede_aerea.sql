-- Malha aérea inicial (seção 2 do enunciado).
-- Novos aeroportos e aerovias entram por novas migrações (V5, V6...), sem mudar o código.

INSERT INTO aeroportos (codigo, nome, vagas) VALUES
    ('POA', 'Aeroporto Internacional Salgado Filho - Porto Alegre', 41),
    ('FLN', 'Aeroporto Internacional de Florianópolis/Hercílio Luz', 16),
    ('CWB', 'Aeroporto Internacional Afonso Pena - Curitiba', 26),
    ('GRU', 'Aeroporto Internacional de São Paulo/Guarulhos', 115);

INSERT INTO aerovias (id, origem_codigo, destino_codigo, distancia_km) VALUES
    ('R1', 'POA', 'FLN', 363),
    ('R2', 'POA', 'GRU', 865),
    ('R3', 'GRU', 'POA', 865),
    ('R4', 'FLN', 'CWB', 245),
    ('R5', 'CWB', 'FLN', 245),
    ('R6', 'CWB', 'GRU', 360),
    ('R7', 'GRU', 'CWB', 360),
    ('R8', 'FLN', 'POA', 363);
