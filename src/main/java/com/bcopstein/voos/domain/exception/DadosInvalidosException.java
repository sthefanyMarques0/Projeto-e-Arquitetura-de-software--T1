package com.bcopstein.voos.domain.exception;

/** Dados informados não formam um objeto de domínio válido (ex.: pouso sem permanência). */
public class DadosInvalidosException extends RegraDeNegocioException {
    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
