package com.bcopstein.voos.adapter.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Sequência ordenada de trechos. Substitui o percurso anterior inteiro. */
public record PercursoRequest(@NotNull List<@Valid @NotNull TrechoRequest> trechos) {}
