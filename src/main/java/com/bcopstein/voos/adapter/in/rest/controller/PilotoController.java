package com.bcopstein.voos.adapter.in.rest.controller;

import com.bcopstein.voos.adapter.in.rest.dto.PilotoResponse;
import com.bcopstein.voos.adapter.in.rest.mapper.RedeAereaRestMapper;
import com.bcopstein.voos.application.port.in.ConsultarPilotos;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** UC03. */
@RestController
public class PilotoController {
    private final ConsultarPilotos consultarPilotos;

    public PilotoController(ConsultarPilotos consultarPilotos) {
        this.consultarPilotos = consultarPilotos;
    }

    @GetMapping("/api/pilotos")
    public List<PilotoResponse> consultar() {
        return consultarPilotos.consultar().stream().map(RedeAereaRestMapper::toResponse).toList();
    }
}
