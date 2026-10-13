package com.bcopstein.voos.controller;

import com.bcopstein.voos.dominio.*;
import com.bcopstein.voos.dto.NovoPlanoRequest;
import com.bcopstein.voos.dto.NovoTrechoRequest;
import com.bcopstein.voos.repositorio.*;
import com.bcopstein.voos.servico.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/planos")
public class PlanoDeVooController {
    private final PlanoDeVooRepository planoRepo;
    private final PilotoRepository pilotoRepo;
    private final AeronaveRepository aeronaveRepo;
    private final AeroportoRepository aeroportoRepo;
    private final AeroviaRepository aeroviaRepo;
    private final ServicoCalculoHorarios servicoCalculo;
    private final ServicoValidacaoPlano servicoValidacao;
    private final ServicoAprovacaoPlano servicoAprovacao;

    public PlanoDeVooController(PlanoDeVooRepository pr, PilotoRepository plr, AeronaveRepository ar,
                                AeroportoRepository apr, AeroviaRepository avr, ServicoCalculoHorarios sc,
                                ServicoValidacaoPlano sv, ServicoAprovacaoPlano sa) {
        this.planoRepo = pr;
        this.pilotoRepo = plr;
        this.aeronaveRepo = ar;
        this.aeroportoRepo = apr;
        this.aeroviaRepo = avr;
        this.servicoCalculo = sc;
        this.servicoValidacao = sv;
        this.servicoAprovacao = sa;
    }

    // UC09 — Criar plano de voo (em edição)
    @PostMapping
    public ResponseEntity<?> criarPlano(@Valid @RequestBody NovoPlanoRequest req) {
        var piloto = pilotoRepo.findById(req.pilotoId());
        var aeronave = aeronaveRepo.findById(req.prefixoAeronave());
        var origem = aeroportoRepo.findById(req.origemCodigo());
        var destino = aeroportoRepo.findById(req.destinoCodigo());

        if (piloto.isEmpty() || aeronave.isEmpty() || origem.isEmpty() || destino.isEmpty()) {
            return ResponseEntity.badRequest().body("Piloto, aeronave ou aeroportos informados não foram encontrados.");
        }

        PlanoDeVoo plano = new PlanoDeVoo(req.partidaPrevista(), piloto.get(), aeronave.get(), origem.get(), destino.get());
        PlanoDeVoo salvo = planoRepo.save(plano);
        return ResponseEntity.created(URI.create("/planos/" + salvo.getId())).body(salvo);
    }

    // UC11 & UC12 — Adicionar trecho ao percurso com definicao de horario, altitude, vaga e calculo de chegada
    @PostMapping("/{id}/trechos")
    public ResponseEntity<?> adicionarTrecho(@PathVariable Long id, @Valid @RequestBody NovoTrechoRequest req) {
        var optPlano = planoRepo.findById(id);
        if (optPlano.isEmpty()) return ResponseEntity.notFound().build();

        PlanoDeVoo plano = optPlano.get();
        if (plano.getSituacao() != SituacaoPlano.EM_EDICAO) {
            return ResponseEntity.badRequest().body("Somente planos em edição podem ser alterados.");
        }

        var optAerovia = aeroviaRepo.findById(req.aeroviaId());
        if (optAerovia.isEmpty()) return ResponseEntity.badRequest().body("Aerovia não encontrada.");

        Trecho trecho = new Trecho(optAerovia.get(), req.altitude(), req.entradaPrevista(),
                req.tipoPassagem(), req.tempoPermanencia(), req.vagaPretendida());

        // Horario de chegada calculado pelo sistema a partir da distancia e velocidade
        LocalDateTime chegada = servicoCalculo.calcularChegada(trecho, plano.getAeronave());
        trecho.setChegadaPrevista(chegada);

        plano.adicionarTrecho(trecho);
        return ResponseEntity.ok(planoRepo.save(plano));
    }

    // UC11 — Remover trecho do percurso
    @DeleteMapping("/{id}/trechos/{trechoId}")
    public ResponseEntity<?> removerTrecho(@PathVariable Long id, @PathVariable Long trechoId) {
        var optPlano = planoRepo.findById(id);
        if (optPlano.isEmpty()) return ResponseEntity.notFound().build();

        PlanoDeVoo plano = optPlano.get();
        if (plano.getSituacao() != SituacaoPlano.EM_EDICAO) {
            return ResponseEntity.badRequest().body("Somente planos em edição podem ser alterados.");
        }

        plano.getTrechos().removeIf(t -> t.getId().equals(trechoId));
        return ResponseEntity.ok(planoRepo.save(plano));
    }

    // UC15 — Validar plano (sem reservar)
    @PostMapping("/{id}/validar")
    public ResponseEntity<ResultadoValidacao> validarPlano(@PathVariable Long id) {
        return planoRepo.findById(id)
                .map(plano -> ResponseEntity.ok(servicoValidacao.validar(plano)))
                .orElse(ResponseEntity.notFound().build());
    }

    // UC16 — Aprovar plano (com reserva integral de recursos)
    @PostMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovarPlano(@PathVariable Long id) {
        var optPlano = planoRepo.findById(id);
        if (optPlano.isEmpty()) return ResponseEntity.notFound().build();

        try {
            PlanoDeVoo aprovado = servicoAprovacao.aprovar(optPlano.get(), LocalDateTime.now());
            return ResponseEntity.ok(aprovado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC17 — Consultar plano por identificador
    @GetMapping("/{id}")
    public ResponseEntity<PlanoDeVoo> buscarPorId(@PathVariable Long id) {
        return planoRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // UC18 — Consultar planos por situação
    @GetMapping
    public List<PlanoDeVoo> listarPlanos(@RequestParam(required = false) SituacaoPlano situacao) {
        return situacao != null ? planoRepo.findBySituacao(situacao) : planoRepo.findAll();
    }

    // UC19 — Abandonar plano em edição
    @PostMapping("/{id}/abandonar")
    public ResponseEntity<?> abandonarPlano(@PathVariable Long id) {
        var optPlano = planoRepo.findById(id);
        if (optPlano.isEmpty()) return ResponseEntity.notFound().build();
        try {
            servicoAprovacao.abandonar(optPlano.get());
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC20 — Cancelar plano aprovado futuro (liberando reservas)
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPlano(@PathVariable Long id) {
        var optPlano = planoRepo.findById(id);
        if (optPlano.isEmpty()) return ResponseEntity.notFound().build();
        try {
            servicoAprovacao.cancelar(optPlano.get(), LocalDateTime.now());
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
