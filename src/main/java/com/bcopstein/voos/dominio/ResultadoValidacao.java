package com.bcopstein.voos.dominio;

import java.util.ArrayList;
import java.util.List;

public class ResultadoValidacao {
    private boolean valido;
    private List<String> erros = new ArrayList<>();

    public ResultadoValidacao() {
        this.valido = true;
    }

    public boolean isValido() { return valido; }
    public List<String> getErros() { return erros; }

    public void adicionarErro(String erro) {
        this.valido = false;
        this.erros.add(erro);
    }
}
