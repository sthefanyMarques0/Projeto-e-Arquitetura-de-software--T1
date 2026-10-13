package com.bcopstein.voos.controller;

import com.bcopstein.voos.dominio.Aeroporto;
import com.bcopstein.voos.dominio.Aerovia;
import com.bcopstein.voos.repositorio.AeroportoRepository;
import com.bcopstein.voos.repositorio.AeroviaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping
public class RedeAereaController {
    private final AeroportoRepository aeroportoRepo;
    private final AeroviaRepository aeroviaRepo;

    public RedeAereaController(AeroportoRepository aeroportoRepo, AeroviaRepository aeroviaRepo) {
        this.aeroportoRepo = aeroportoRepo;
        this.aeroviaRepo = aeroviaRepo;
    }

    // UC01 — Consultar aeroportos
    @GetMapping("/aeroportos")
    public List<Aeroporto> listarAeroportos() {
        return aeroportoRepo.findAll();
    }

    // UC02 — Cadastrar aeroporto
    @PostMapping("/aeroportos")
    public ResponseEntity<Aeroporto> cadastrarAeroporto(@RequestBody Aeroporto aeroporto) {
        Aeroporto salvo = aeroportoRepo.save(aeroporto);
        return ResponseEntity.created(URI.create("/aeroportos/" + salvo.getCodigo())).body(salvo);
    }

    // UC03 — Consultar aerovias
    @GetMapping("/aerovias")
    public List<Aerovia> listarAerovias() {
        return aeroviaRepo.findAll();
    }

    // UC04 — Cadastrar aerovia (Imutavel: nao admite alteracao ou exclusao posterior)
    @PostMapping("/aerovias")
    public ResponseEntity<?> cadastrarAerovia(@RequestBody Aerovia aerovia) {
        if (aeroviaRepo.existsById(aerovia.getId())) {
            return ResponseEntity.badRequest().body("Aerovia com id " + aerovia.getId() + " já existe.");
        }
        Aerovia salva = aeroviaRepo.save(aerovia);
        return ResponseEntity.created(URI.create("/aerovias/" + salva.getId())).body(salva);
    }
}
