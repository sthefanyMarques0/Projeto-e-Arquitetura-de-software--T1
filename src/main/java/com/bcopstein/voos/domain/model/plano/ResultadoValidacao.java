package com.bcopstein.voos.domain.model.plano;

import java.util.ArrayList;
import java.util.List;

/** Resultado de uma validação: válido somente se nenhum erro foi registrado. */
public class ResultadoValidacao {
    private final List<String> erros = new ArrayList<>();

    public void adicionarErro(String erro) {
        erros.add(erro);
    }

    public void adicionarErros(List<String> novos) {
        erros.addAll(novos);
    }

    public boolean isValido() {
        return erros.isEmpty();
    }

    public List<String> getErros() {
        return List.copyOf(erros);
    }
}
