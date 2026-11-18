package com.bcopstein.cadastro;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class CadastroController {
    private final JdbcTemplate jdbc;

    public CadastroController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/aeroportos")
    public List<AeroportoResponse> aeroportos() {
        return jdbc.query("select codigo, nome, vagas from aeroportos order by codigo",
                (rs, row) -> new AeroportoResponse(rs.getString("codigo"), rs.getString("nome"), rs.getInt("vagas")));
    }

    @GetMapping("/aeroportos/{codigo}")
    public AeroportoResponse aeroporto(@PathVariable String codigo) {
        return jdbc.query("select codigo, nome, vagas from aeroportos where codigo = ?", (rs, row) ->
                new AeroportoResponse(rs.getString("codigo"), rs.getString("nome"), rs.getInt("vagas")), codigo)
                .stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aeroporto não encontrado: " + codigo));
    }

    @GetMapping("/aeroportos/{codigo}/vagas")
    public VagasResponse vagas(@PathVariable String codigo) {
        AeroportoResponse aeroporto = aeroporto(codigo);
        return new VagasResponse(codigo, aeroporto.vagas(),
                IntStream.rangeClosed(1, aeroporto.vagas()).boxed().toList());
    }

    @PostMapping("/aeroportos")
    public ResponseEntity<AeroportoResponse> cadastrarAeroporto(@RequestBody NovoAeroporto request) {
        if (request.codigo() == null || request.codigo().isBlank() || request.nome() == null || request.nome().isBlank()
                || request.vagas() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código, nome e quantidade de vagas válida são obrigatórios.");
        }
        jdbc.update("insert into aeroportos (codigo, nome, vagas) values (?, ?, ?)",
                request.codigo(), request.nome(), request.vagas());
        return ResponseEntity.created(URI.create("/api/aeroportos/" + request.codigo()))
                .body(new AeroportoResponse(request.codigo(), request.nome(), request.vagas()));
    }

    @GetMapping("/aerovias")
    public List<AeroviaResponse> aerovias(@RequestParam(required = false) String origem,
                                           @RequestParam(required = false) String destino) {
        return jdbc.query("select id, origem_codigo, destino_codigo, distancia_km from aerovias "
                        + "where (? is null or origem_codigo = ?) and (? is null or destino_codigo = ?) order by id",
                (rs, row) -> new AeroviaResponse(rs.getString("id"), rs.getString("origem_codigo"),
                        rs.getString("destino_codigo"), rs.getBigDecimal("distancia_km")),
                origem, origem, destino, destino);
    }

    @GetMapping("/aerovias/{id}")
    public AeroviaResponse aerovia(@PathVariable String id) {
        return jdbc.query("select id, origem_codigo, destino_codigo, distancia_km from aerovias where id = ?",
                (rs, row) -> new AeroviaResponse(rs.getString("id"), rs.getString("origem_codigo"),
                        rs.getString("destino_codigo"), rs.getBigDecimal("distancia_km")), id)
                .stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aerovia não encontrada: " + id));
    }

    @PostMapping("/aerovias")
    public ResponseEntity<AeroviaResponse> cadastrarAerovia(@RequestBody NovaAerovia request) {
        if (request.id() == null || request.id().isBlank() || request.origem() == null || request.destino() == null
                || request.distanciaKm() == null || request.distanciaKm().signum() <= 0
                || request.origem().equals(request.destino())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identificador, aeroportos distintos e distância positiva são obrigatórios.");
        }
        aeroporto(request.origem());
        aeroporto(request.destino());
        jdbc.update("insert into aerovias (id, origem_codigo, destino_codigo, distancia_km) values (?, ?, ?, ?)",
                request.id(), request.origem(), request.destino(), request.distanciaKm());
        return ResponseEntity.created(URI.create("/api/aerovias/" + request.id()))
                .body(new AeroviaResponse(request.id(), request.origem(), request.destino(), request.distanciaKm()));
    }

    public record NovoAeroporto(String codigo, String nome, int vagas) {}
    public record NovaAerovia(String id, String origem, String destino, BigDecimal distanciaKm) {}
}

@RestControllerAdvice
class CadastroErroHandler {
    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<String> duplicado(DuplicateKeyException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("O identificador já está cadastrado.");
    }
}
