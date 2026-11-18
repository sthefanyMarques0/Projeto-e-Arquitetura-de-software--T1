CREATE TABLE aeroportos (
    codigo VARCHAR(10) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    vagas INTEGER NOT NULL CHECK (vagas >= 0)
);

CREATE TABLE aerovias (
    id VARCHAR(20) PRIMARY KEY,
    origem_codigo VARCHAR(10) NOT NULL REFERENCES aeroportos(codigo),
    destino_codigo VARCHAR(10) NOT NULL REFERENCES aeroportos(codigo),
    distancia_km NUMERIC(38, 2) NOT NULL CHECK (distancia_km > 0),
    CHECK (origem_codigo <> destino_codigo)
);

INSERT INTO aeroportos (codigo, nome, vagas) VALUES
    ('POA', 'Aeroporto Internacional Salgado Filho - Porto Alegre', 41),
    ('FLN', 'Aeroporto Internacional de Florianópolis/Hercílio Luz', 16),
    ('CWB', 'Aeroporto Internacional Afonso Pena - Curitiba', 26),
    ('GRU', 'Aeroporto Internacional de São Paulo/Guarulhos', 115);

INSERT INTO aerovias (id, origem_codigo, destino_codigo, distancia_km) VALUES
    ('R1', 'POA', 'FLN', 363), ('R2', 'POA', 'GRU', 865),
    ('R3', 'GRU', 'POA', 865), ('R4', 'FLN', 'CWB', 245),
    ('R5', 'CWB', 'FLN', 245), ('R6', 'CWB', 'GRU', 360),
    ('R7', 'GRU', 'CWB', 360), ('R8', 'FLN', 'POA', 363);
