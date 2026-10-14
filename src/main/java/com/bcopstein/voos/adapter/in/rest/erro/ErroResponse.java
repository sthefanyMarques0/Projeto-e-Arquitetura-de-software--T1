package com.bcopstein.voos.adapter.in.rest.erro;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErroResponse(int status, String erro, String mensagem, List<String> detalhes) {}
