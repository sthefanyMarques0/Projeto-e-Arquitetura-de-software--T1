package com.bcopstein.voos.controller;

import com.bcopstein.voos.dominio.Aeronave;
import com.bcopstein.voos.dominio.Piloto;
import com.bcopstein.voos.repositorio.AeronaveRepository;
import com.bcopstein.voos.repositorio.PilotoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping
public class PilotoAeronaveController {
    private final PilotoRepository pilotoRepo;
    private final AeronaveRepository aeronaveRepo;

    public PilotoAeronaveController(PilotoRepository pilotoRepo, AeronaveRepository aeronaveRepo) {
        this.pilotoRepo = pilotoRepo;
        this.aeronaveRepo = aeronaveRepo;
    }

    // UC05 — Consultar pilotos
    @GetMapping("/pilotos")
    public List<Piloto> listarPilotos() {
        return pilotoRepo.findAll();
    }

    // UC06 — Cadastrar piloto
    @PostMapping("/pilotos")
    public ResponseEntity<Piloto> cadastrarPiloto(@RequestBody Piloto piloto) {
        Piloto salvo = pilotoRepo.save(piloto);
        return ResponseEntity.created(URI.create("/pilotos/" + salvo.getId())).body(salvo);
    }

    // UC07 — Consultar aeronaves
    @GetMapping("/aeronaves")
    public List<Aeronave> listarAeronaves() {
        return aeronaveRepo.findAll();
    }

    // UC08 — Cadastrar aeronave
    @PostMapping("/aeronaves")
    public ResponseEntity<Aeronave> cadastrarAeronave(@RequestBody Aeronave aeronave) {
        Aeronave salva = aeronaveRepo.save(aeronave);
        return ResponseEntity.created(URI.create("/aeronaves/" + salva.getPrefixo())).body(salva);
    }
}
