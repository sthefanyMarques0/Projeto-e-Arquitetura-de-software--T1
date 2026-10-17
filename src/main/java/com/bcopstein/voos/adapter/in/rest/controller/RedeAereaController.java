package com.bcopstein.voos.adapter.in.rest.controller;

import com.bcopstein.voos.adapter.in.rest.dto.AeroportoResponse;
import com.bcopstein.voos.adapter.in.rest.dto.AeroviaResponse;
import com.bcopstein.voos.adapter.in.rest.mapper.RedeAereaRestMapper;
import com.bcopstein.voos.application.port.in.ConsultarAeroportos;
import com.bcopstein.voos.application.port.in.ConsultarAerovias;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** UC01 e UC02. A rede aérea vem da carga inicial (Flyway); a API apenas consulta. */
@RestController
public class RedeAereaController {
    private final ConsultarAeroportos consultarAeroportos;
    private final ConsultarAerovias consultarAerovias;

    public RedeAereaController(ConsultarAeroportos consultarAeroportos, ConsultarAerovias consultarAerovias) {
        this.consultarAeroportos = consultarAeroportos;
        this.consultarAerovias = consultarAerovias;
    }

    @GetMapping("/api/aeroportos")
    public List<AeroportoResponse> aeroportos() {
        return consultarAeroportos.consultar().stream().map(RedeAereaRestMapper::toResponse).toList();
    }

    @GetMapping("/api/aerovias")
    public List<AeroviaResponse> aerovias() {
        return consultarAerovias.consultar().stream().map(RedeAereaRestMapper::toResponse).toList();
    }
}
