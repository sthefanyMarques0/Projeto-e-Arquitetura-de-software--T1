package com.bcopstein.voos.domain.exception;

/** A operação não é permitida na situação atual do plano (ex.: alterar plano aprovado). */
public class OperacaoNaoPermitidaException extends RegraDeNegocioException {
    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
