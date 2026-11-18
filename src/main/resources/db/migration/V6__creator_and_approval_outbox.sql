ALTER TABLE planos_de_voo
    ADD COLUMN criado_por VARCHAR(255) NOT NULL DEFAULT 'legado';

CREATE TABLE outbox_aprovacoes (
    evento_id VARCHAR(36) PRIMARY KEY,
    plano_id BIGINT NOT NULL,
    payload CLOB NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    publicado_em TIMESTAMP,
    tentativas INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX ix_outbox_aprovacoes_pendentes
    ON outbox_aprovacoes (publicado_em, criado_em);
