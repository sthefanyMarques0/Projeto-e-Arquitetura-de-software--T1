package com.bcopstein.registro;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/aprovacoes")
public class AprovacaoQueryController {
    private final AprovacaoRepository repository;
    private final ObjectMapper mapper;

    public AprovacaoQueryController(AprovacaoRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @GetMapping
    public List<JsonNode> listar() {
        return repository.findAllByOrderByRecebidoEmDesc().stream().map(this::payload).toList();
    }

    @GetMapping("/{planoId}")
    public JsonNode porPlano(@PathVariable Long planoId) {
        return repository.findByPlanoId(planoId).map(this::payload)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aprovação do plano " + planoId + " ainda não foi registrada."));
    }

    private JsonNode payload(AprovacaoEntity record) {
        try {
            return mapper.readTree(record.getPayload());
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("Payload salvo inválido para evento " + record.getEventoId(), e);
        }
    }
}
