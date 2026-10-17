package com.bcopstein.voos.adapter.in.rest.dto;

import java.math.BigDecimal;

public record AeroviaResponse(String id, String origem, String destino, BigDecimal distanciaKm) {}
