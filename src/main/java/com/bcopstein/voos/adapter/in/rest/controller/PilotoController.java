package com.bcopstein.voos.adapter.in.rest.controller;

import com.bcopstein.voos.adapter.in.rest.dto.PilotoRequest;
import com.bcopstein.voos.adapter.in.rest.dto.PilotoResponse;
import com.bcopstein.voos.adapter.in.rest.mapper.RedeAereaRestMapper;
import com.bcopstein.voos.application.port.in.CadastrarPiloto;
import com.bcopstein.voos.application.port.in.ConsultarPilotos;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** UC03: consulta e cadastro de pilotos. */
@RestController
public class PilotoController {
    private final ConsultarPilotos consultarPilotos;
    private final CadastrarPiloto cadastrarPiloto;

    public PilotoController(ConsultarPilotos consultarPilotos, CadastrarPiloto cadastrarPiloto) {
        this.consultarPilotos = consultarPilotos;
        this.cadastrarPiloto = cadastrarPiloto;
    }

    @GetMapping("/api/pilotos")
    public List<PilotoResponse> consultar() {
        return consultarPilotos.consultar().stream().map(RedeAereaRestMapper::toResponse).toList();
    }

    @PostMapping("/api/pilotos")
    public ResponseEntity<PilotoResponse> cadastrar(@Valid @RequestBody PilotoRequest request) {
        Piloto piloto = cadastrarPiloto.cadastrar(request.id(), request.nome());
        return ResponseEntity.created(URI.create("/api/pilotos/" + piloto.getId()))
                .body(RedeAereaRestMapper.toResponse(piloto));
    }
}
