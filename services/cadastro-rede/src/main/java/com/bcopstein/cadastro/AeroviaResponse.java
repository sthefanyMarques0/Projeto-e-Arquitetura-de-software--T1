package com.bcopstein.cadastro;

import java.math.BigDecimal;

public record AeroviaResponse(String id, String origem, String destino, BigDecimal distanciaKm) {}
