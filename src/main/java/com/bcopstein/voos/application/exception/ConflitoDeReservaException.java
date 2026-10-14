package com.bcopstein.voos.application.exception;

/**
 * Outro plano reservou um recurso entre a validação e a gravação das reservas.
 * A transação é desfeita: nenhuma reserva da tentativa permanece.
 */
public class ConflitoDeReservaException extends RuntimeException {
    public ConflitoDeReservaException(String mensagem) {
        super(mensagem);
    }
}
