CREATE TABLE aprovacoes (
    evento_id VARCHAR(36) PRIMARY KEY,
    plano_id BIGINT NOT NULL UNIQUE,
    recebido_em TIMESTAMP NOT NULL,
    payload CLOB NOT NULL
);
