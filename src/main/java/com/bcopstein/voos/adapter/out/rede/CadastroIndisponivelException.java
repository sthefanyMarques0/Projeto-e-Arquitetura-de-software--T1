package com.bcopstein.voos.adapter.out.rede;

public class CadastroIndisponivelException extends RuntimeException {
    public CadastroIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
