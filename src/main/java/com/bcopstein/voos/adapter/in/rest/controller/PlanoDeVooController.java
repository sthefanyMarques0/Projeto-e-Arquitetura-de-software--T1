package com.bcopstein.voos.adapter.in.rest.controller;

import com.bcopstein.voos.adapter.in.rest.dto.PercursoRequest;
import com.bcopstein.voos.adapter.in.rest.dto.PlanoRequest;
import com.bcopstein.voos.adapter.in.rest.dto.PlanoResponse;
import com.bcopstein.voos.adapter.in.rest.dto.ReservaResponse;
import com.bcopstein.voos.adapter.in.rest.dto.ValidacaoResponse;
import com.bcopstein.voos.adapter.in.rest.mapper.PlanoRestMapper;
import com.bcopstein.voos.application.port.in.AbandonarPlano;
import com.bcopstein.voos.application.port.in.AprovarPlano;
import com.bcopstein.voos.application.port.in.CancelarPlano;
import com.bcopstein.voos.application.port.in.ConsultarPlanos;
import com.bcopstein.voos.application.port.in.CriarPlano;
import com.bcopstein.voos.application.port.in.DefinirPercurso;
import com.bcopstein.voos.application.port.in.EditarPlano;
import com.bcopstein.voos.application.port.in.ValidarPlano;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.SituacaoPlano;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** UC05–UC08 e UC11–UC16: ciclo de vida do plano de voo. Só converte DTOs e chama os casos de uso. */
@RestController
@RequestMapping("/api/planos")
public class PlanoDeVooController {
    private final CriarPlano criarPlano;
    private final EditarPlano editarPlano;
    private final DefinirPercurso definirPercurso;
    private final ValidarPlano validarPlano;
    private final AprovarPlano aprovarPlano;
    private final ConsultarPlanos consultarPlanos;
    private final AbandonarPlano abandonarPlano;
    private final CancelarPlano cancelarPlano;

    public PlanoDeVooController(CriarPlano criarPlano, EditarPlano editarPlano, DefinirPercurso definirPercurso,
                                ValidarPlano validarPlano, AprovarPlano aprovarPlano, ConsultarPlanos consultarPlanos,
                                AbandonarPlano abandonarPlano, CancelarPlano cancelarPlano) {
        this.criarPlano = criarPlano;
        this.editarPlano = editarPlano;
        this.definirPercurso = definirPercurso;
        this.validarPlano = validarPlano;
        this.aprovarPlano = aprovarPlano;
        this.consultarPlanos = consultarPlanos;
        this.abandonarPlano = abandonarPlano;
        this.cancelarPlano = cancelarPlano;
    }

    /** UC05 — Criar plano de voo (nasce em edição). */
    @PostMapping
    public ResponseEntity<PlanoResponse> criar(@Valid @RequestBody PlanoRequest request) {
        PlanoDeVoo plano = criarPlano.criar(PlanoRestMapper.toCommand(request));
        return ResponseEntity.created(URI.create("/api/planos/" + plano.getId())).body(PlanoRestMapper.toResponse(plano));
    }

    /** UC06 — Editar dados do plano em edição. */
    @PutMapping("/{id}")
    public PlanoResponse editar(@PathVariable Long id, @Valid @RequestBody PlanoRequest request) {
        return PlanoRestMapper.toResponse(editarPlano.editar(id, PlanoRestMapper.toCommand(request)));
    }

    /** UC07 e UC08 — Definir/alterar o percurso e os dados de cada trecho. */
    @PutMapping("/{id}/percurso")
    public PlanoResponse definirPercurso(@PathVariable Long id, @Valid @RequestBody PercursoRequest request) {
        return PlanoRestMapper.toResponse(definirPercurso.definir(id,
                request.trechos().stream().map(PlanoRestMapper::toCommand).toList()));
    }

    /** UC11 — Validar plano (não reserva nem muda a situação). */
    @PostMapping("/{id}/validacao")
    public ValidacaoResponse validar(@PathVariable Long id) {
        return PlanoRestMapper.toResponse(id, validarPlano.validar(id));
    }

    /** UC12 — Aprovar plano, reservando todos os recursos (tudo ou nada). */
    @PostMapping("/{id}/aprovacao")
    public PlanoResponse aprovar(@PathVariable Long id) {
        return PlanoRestMapper.toResponse(aprovarPlano.aprovar(id));
    }

    /** UC13 — Consultar plano por identificador. */
    @GetMapping("/{id}")
    public PlanoResponse consultar(@PathVariable Long id) {
        return PlanoRestMapper.toResponse(consultarPlanos.porId(id));
    }

    /** UC14 — Consultar planos por situação (sem parâmetro, lista todos). */
    @GetMapping
    public List<PlanoResponse> listar(@RequestParam(required = false) SituacaoPlano situacao) {
        return consultarPlanos.porSituacao(situacao).stream().map(PlanoRestMapper::toResponse).toList();
    }

    /** Reservas efetivadas pelo plano (vazias enquanto em edição). */
    @GetMapping("/{id}/reservas")
    public List<ReservaResponse> reservas(@PathVariable Long id) {
        return consultarPlanos.reservasDoPlano(id).stream().map(PlanoRestMapper::toResponse).toList();
    }

    /** UC15 — Abandonar plano em edição. */
    @PostMapping("/{id}/abandono")
    public PlanoResponse abandonar(@PathVariable Long id) {
        return PlanoRestMapper.toResponse(abandonarPlano.abandonar(id));
    }

    /** UC16 — Cancelar plano aprovado futuro, liberando as reservas. */
    @PostMapping("/{id}/cancelamento")
    public PlanoResponse cancelar(@PathVariable Long id) {
        return PlanoRestMapper.toResponse(cancelarPlano.cancelar(id));
    }
}
