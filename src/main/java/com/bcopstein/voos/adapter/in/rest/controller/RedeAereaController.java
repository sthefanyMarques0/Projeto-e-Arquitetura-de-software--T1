package com.bcopstein.voos.adapter.in.rest.controller;

import com.bcopstein.voos.adapter.in.rest.dto.AeroportoRequest;
import com.bcopstein.voos.adapter.in.rest.dto.AeroportoResponse;
import com.bcopstein.voos.adapter.in.rest.dto.AeroviaRequest;
import com.bcopstein.voos.adapter.in.rest.dto.AeroviaResponse;
import com.bcopstein.voos.adapter.in.rest.mapper.RedeAereaRestMapper;
import com.bcopstein.voos.application.port.in.CadastrarAeroporto;
import com.bcopstein.voos.application.port.in.CadastrarAerovia;
import com.bcopstein.voos.application.port.in.ConsultarAeroportos;
import com.bcopstein.voos.application.port.in.ConsultarAerovias;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** UC01 e UC02: consulta e cadastro de aeroportos e aerovias. Aerovias não podem ser alteradas nem excluídas. */
@RestController
public class RedeAereaController {
    private final ConsultarAeroportos consultarAeroportos;
    private final ConsultarAerovias consultarAerovias;
    private final CadastrarAeroporto cadastrarAeroporto;
    private final CadastrarAerovia cadastrarAerovia;

    public RedeAereaController(ConsultarAeroportos consultarAeroportos, ConsultarAerovias consultarAerovias,
                               CadastrarAeroporto cadastrarAeroporto, CadastrarAerovia cadastrarAerovia) {
        this.consultarAeroportos = consultarAeroportos;
        this.consultarAerovias = consultarAerovias;
        this.cadastrarAeroporto = cadastrarAeroporto;
        this.cadastrarAerovia = cadastrarAerovia;
    }

    @GetMapping("/api/aeroportos")
    public List<AeroportoResponse> aeroportos() {
        return consultarAeroportos.consultar().stream().map(RedeAereaRestMapper::toResponse).toList();
    }

    @PostMapping("/api/aeroportos")
    public ResponseEntity<AeroportoResponse> cadastrarAeroporto(@Valid @RequestBody AeroportoRequest request) {
        Aeroporto aeroporto = cadastrarAeroporto.cadastrar(request.codigo(), request.nome(), request.vagas());
        return ResponseEntity.created(URI.create("/api/aeroportos/" + aeroporto.getCodigo()))
                .body(RedeAereaRestMapper.toResponse(aeroporto));
    }

    @GetMapping("/api/aerovias")
    public List<AeroviaResponse> aerovias() {
        return consultarAerovias.consultar().stream().map(RedeAereaRestMapper::toResponse).toList();
    }

    @PostMapping("/api/aerovias")
    public ResponseEntity<AeroviaResponse> cadastrarAerovia(@Valid @RequestBody AeroviaRequest request) {
        Aerovia aerovia = cadastrarAerovia.cadastrar(request.id(), request.origem(), request.destino(), request.distanciaKm());
        return ResponseEntity.created(URI.create("/api/aerovias/" + aerovia.getId()))
                .body(RedeAereaRestMapper.toResponse(aerovia));
    }
}
