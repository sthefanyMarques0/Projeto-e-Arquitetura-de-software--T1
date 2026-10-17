package com.bcopstein.voos.adapter.in.rest.controller;

import com.bcopstein.voos.adapter.in.rest.dto.AeronaveResponse;
import com.bcopstein.voos.adapter.in.rest.mapper.AeronaveRestMapper;
import com.bcopstein.voos.application.port.in.ConsultarAeronaves;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** UC04. */
@RestController
public class AeronaveController {
    private final ConsultarAeronaves consultarAeronaves;

    public AeronaveController(ConsultarAeronaves consultarAeronaves) {
        this.consultarAeronaves = consultarAeronaves;
    }

    @GetMapping("/api/aeronaves")
    public List<AeronaveResponse> consultar() {
        return consultarAeronaves.consultar().stream().map(AeronaveRestMapper::toResponse).toList();
    }
}
