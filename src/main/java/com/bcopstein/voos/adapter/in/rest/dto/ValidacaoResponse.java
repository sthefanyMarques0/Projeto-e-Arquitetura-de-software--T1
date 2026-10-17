package com.bcopstein.voos.adapter.in.rest.dto;

import java.util.List;

public record ValidacaoResponse(Long planoId, boolean valido, List<String> erros) {}
