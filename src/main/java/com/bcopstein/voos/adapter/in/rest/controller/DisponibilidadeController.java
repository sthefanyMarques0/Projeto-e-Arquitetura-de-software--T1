package com.bcopstein.voos.adapter.in.rest.controller;

import com.bcopstein.voos.adapter.in.rest.dto.AltitudesDisponiveisResponse;
import com.bcopstein.voos.adapter.in.rest.dto.VagasDisponiveisResponse;
import com.bcopstein.voos.application.port.in.ConsultarDisponibilidade;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** UC09 e UC10. A consulta mostra a situação no momento e não reserva recursos. */
@RestController
public class DisponibilidadeController {
    private final ConsultarDisponibilidade consultarDisponibilidade;

    public DisponibilidadeController(ConsultarDisponibilidade consultarDisponibilidade) {
        this.consultarDisponibilidade = consultarDisponibilidade;
    }

    @GetMapping("/api/disponibilidade/aerovias/{aeroviaId}/altitudes")
    public AltitudesDisponiveisResponse altitudes(
            @PathVariable String aeroviaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(required = false) String aeronave) {
        List<Integer> livres = consultarDisponibilidade.altitudesDisponiveis(aeroviaId, inicio, fim, aeronave);
        return new AltitudesDisponiveisResponse(aeroviaId, inicio, fim, aeronave, livres);
    }

    @GetMapping("/api/disponibilidade/aeroportos/{codigo}/vagas")
    public VagasDisponiveisResponse vagas(
            @PathVariable String codigo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<Integer> livres = consultarDisponibilidade.vagasDisponiveis(codigo, inicio, fim);
        return new VagasDisponiveisResponse(codigo, inicio, fim, livres.size(), livres);
    }
}
