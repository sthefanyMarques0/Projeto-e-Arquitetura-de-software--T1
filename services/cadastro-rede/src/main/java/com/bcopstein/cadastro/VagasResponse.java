package com.bcopstein.cadastro;

import java.util.List;

public record VagasResponse(String aeroportoCodigo, int quantidade, List<Integer> numeros) {}
