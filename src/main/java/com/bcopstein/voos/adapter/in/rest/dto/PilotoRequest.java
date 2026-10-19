package com.bcopstein.voos.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PilotoRequest(@NotNull @Positive Long id, @NotBlank String nome) {}
