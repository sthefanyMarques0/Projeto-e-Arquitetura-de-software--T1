package com.bcopstein.voos.domain.exception;

/** Base das exceções lançadas pelo domínio quando uma regra de negócio é violada. */
public abstract class RegraDeNegocioException extends RuntimeException {
    protected RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
