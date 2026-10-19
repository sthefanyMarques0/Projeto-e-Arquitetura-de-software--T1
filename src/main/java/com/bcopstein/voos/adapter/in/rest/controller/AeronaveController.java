package com.bcopstein.voos.adapter.in.rest.controller;

import com.bcopstein.voos.adapter.in.rest.dto.AeronaveRequest;
import com.bcopstein.voos.adapter.in.rest.dto.AeronaveResponse;
import com.bcopstein.voos.adapter.in.rest.mapper.AeronaveRestMapper;
import com.bcopstein.voos.application.port.in.CadastrarAeronave;
import com.bcopstein.voos.application.port.in.ConsultarAeronaves;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** UC04: consulta e cadastro de aeronaves. Os campos específicos dependem da categoria. */
@RestController
public class AeronaveController {
    private final ConsultarAeronaves consultarAeronaves;
    private final CadastrarAeronave cadastrarAeronave;

    public AeronaveController(ConsultarAeronaves consultarAeronaves, CadastrarAeronave cadastrarAeronave) {
        this.consultarAeronaves = consultarAeronaves;
        this.cadastrarAeronave = cadastrarAeronave;
    }

    @GetMapping("/api/aeronaves")
    public List<AeronaveResponse> consultar() {
        return consultarAeronaves.consultar().stream().map(AeronaveRestMapper::toResponse).toList();
    }

    @PostMapping("/api/aeronaves")
    public ResponseEntity<AeronaveResponse> cadastrar(@Valid @RequestBody AeronaveRequest request) {
        Aeronave aeronave = cadastrarAeronave.cadastrar(AeronaveRestMapper.toCommand(request));
        return ResponseEntity.created(URI.create("/api/aeronaves/" + aeronave.getPrefixo()))
                .body(AeronaveRestMapper.toResponse(aeronave));
    }
}
