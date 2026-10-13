package com.bcopstein.voos.controller;

import com.bcopstein.voos.dominio.*;
import com.bcopstein.voos.repositorio.AeronaveRepository;
import com.bcopstein.voos.repositorio.AeroportoRepository;
import com.bcopstein.voos.repositorio.AeroviaRepository;
import com.bcopstein.voos.servico.ServicoDisponibilidade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/disponibilidade")
public class DisponibilidadeController {
    private final ServicoDisponibilidade servicoDisponibilidade;
    private final AeroviaRepository aeroviaRepo;
    private final AeroportoRepository aeroportoRepo;
    private final AeronaveRepository aeronaveRepo;

    public DisponibilidadeController(ServicoDisponibilidade sd, AeroviaRepository ar, AeroportoRepository ap, AeronaveRepository an) {
        this.servicoDisponibilidade = sd;
        this.aeroviaRepo = ar;
        this.aeroportoRepo = ap;
        this.aeronaveRepo = an;
    }

    // UC13 — Consultar disponibilidade de altitudes em aerovia
    @GetMapping("/altitudes")
    public ResponseEntity<List<Integer>> consultarAltitudes(
            @RequestParam String aeroviaId,
            @RequestParam String prefixoAeronave,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime de,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ate) {
        var aerovia = aeroviaRepo.findById(aeroviaId);
        var aeronave = aeronaveRepo.findById(prefixoAeronave);
        if (aerovia.isEmpty() || aeronave.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Periodo periodo = new Periodo(de, ate);
        return ResponseEntity.ok(servicoDisponibilidade.altitudesDisponiveis(aerovia.get(), periodo, aeronave.get()));
    }

    // UC14 — Consultar disponibilidade de vagas em aeroporto
    @GetMapping("/vagas")
    public ResponseEntity<Integer> consultarVagas(
            @RequestParam String aeroportoCodigo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime de,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ate) {
        var aeroporto = aeroportoRepo.findById(aeroportoCodigo);
        if (aeroporto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Periodo periodo = new Periodo(de, ate);
        return ResponseEntity.ok(servicoDisponibilidade.vagasDisponiveis(aeroporto.get(), periodo));
    }
}
