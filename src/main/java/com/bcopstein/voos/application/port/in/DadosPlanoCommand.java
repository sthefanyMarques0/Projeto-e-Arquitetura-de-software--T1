package com.bcopstein.voos.application.port.in;

import java.time.LocalDateTime;

/** Dados gerais de um plano, usados na criação e na edição. */
public record DadosPlanoCommand(Long pilotoId, String prefixoAeronave, String origem, String destino,
                                LocalDateTime partidaPrevista) {}
