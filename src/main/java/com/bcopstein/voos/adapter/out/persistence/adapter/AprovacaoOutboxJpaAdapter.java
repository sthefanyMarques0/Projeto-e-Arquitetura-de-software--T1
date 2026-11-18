package com.bcopstein.voos.adapter.out.persistence.adapter;

import com.bcopstein.voos.adapter.out.persistence.entity.AprovacaoOutboxEntity;
import com.bcopstein.voos.adapter.out.persistence.repository.AprovacaoOutboxJpaRepository;
import com.bcopstein.voos.application.integration.AprovacaoEvent;
import com.bcopstein.voos.application.port.out.AprovacaoOutboxPort;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.aeronave.AeronaveCarga;
import com.bcopstein.voos.domain.model.aeronave.AeronaveComercial;
import com.bcopstein.voos.domain.model.aeronave.AeronaveParticular;
import com.bcopstein.voos.domain.model.aeronave.AeronavePassageiros;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.Trecho;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.stream.IntStream;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AprovacaoOutboxJpaAdapter implements AprovacaoOutboxPort {
    private final AprovacaoOutboxJpaRepository repository;
    private final ObjectMapper objectMapper;

    public AprovacaoOutboxJpaAdapter(AprovacaoOutboxJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void registrar(String eventoId, PlanoDeVoo plano, LocalDateTime aprovadoEm) {
        if (repository.existsById(eventoId)) {
            return;
        }
        AprovacaoEvent evento = map(eventoId, plano, aprovadoEm);
        try {
            repository.save(new AprovacaoOutboxEntity(eventoId, plano.getId(),
                    objectMapper.writeValueAsString(evento), aprovadoEm));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Não foi possível serializar o evento de aprovação do plano " + plano.getId(), e);
        }
    }

    @Override
    public boolean contem(String eventoId) {
        return repository.existsById(eventoId);
    }

    private static AprovacaoEvent map(String eventId, PlanoDeVoo plano, LocalDateTime approvedAt) {
        Aeronave aeronave = plano.getAeronave();
        String manutencao = aeronave instanceof AeronaveParticular particular ? particular.getEmpresaManutencao() : null;
        String companhia = aeronave instanceof AeronaveComercial comercial ? comercial.getCompanhiaAerea() : null;
        Integer passageiros = aeronave instanceof AeronavePassageiros comercial ? comercial.getCapacidadeMaximaPassageiros() : null;
        java.math.BigDecimal toneladas = aeronave instanceof AeronaveCarga carga ? carga.getCapacidadeMaximaToneladas() : null;
        AprovacaoEvent.Aeronave aircraft = new AprovacaoEvent.Aeronave(aeronave.getPrefixo(),
                aeronave.getCategoria().name(), aeronave.getVelocidadeCruzeiroKmH(), aeronave.getAutonomiaNominalKm(),
                manutencao, companhia, passageiros, toneladas);
        List<Trecho> trechosDoPlano = plano.getTrechos();
        List<AprovacaoEvent.Trecho> trechos = IntStream.range(0, trechosDoPlano.size())
                .mapToObj(index -> map(trechosDoPlano.get(index), index)).toList();
        return new AprovacaoEvent(eventId, "1.0", plano.getId(), approvedAt, plano.getCriadoPor(),
                new AprovacaoEvent.Piloto(plano.getPiloto().getId(), plano.getPiloto().getNome()), aircraft,
                plano.getOrigem().getCodigo(), plano.getDestino().getCodigo(), plano.getPartidaPrevista(), trechos);
    }

    private static AprovacaoEvent.Trecho map(Trecho trecho, int index) {
        return new AprovacaoEvent.Trecho(index + 1, trecho.getAerovia().getId(),
                trecho.getAerovia().getOrigem().getCodigo(), trecho.getAerovia().getDestino().getCodigo(),
                trecho.getAerovia().getDistanciaKm(), trecho.getAltitude(), trecho.getEntradaPrevista(),
                trecho.getChegadaPrevista(), trecho.getTipoPassagem().name(), trecho.getPermanenciaMinutos(),
                trecho.getVagaPretendida());
    }
}
