package com.bcopstein.voos.application.exception;

import java.util.List;

/** A aprovação foi recusada porque o plano não passou na validação. O plano continua em edição. */
public class PlanoInvalidoException extends RuntimeException {
    private final transient List<String> erros;

    public PlanoInvalidoException(List<String> erros) {
        super("O plano não atende às regras e não foi aprovado; continua em edição.");
        this.erros = List.copyOf(erros);
    }

    public List<String> getErros() { return erros; }
}
