package com.bcopstein.voos.adapter.out.persistence.adapter;

import com.bcopstein.voos.adapter.out.persistence.entity.PlanoDeVooEntity;
import com.bcopstein.voos.adapter.out.persistence.entity.TrechoEmbeddable;
import com.bcopstein.voos.adapter.out.persistence.repository.PlanoDeVooJpaRepository;
import com.bcopstein.voos.application.port.out.ConsultaAeronavesPort;
import com.bcopstein.voos.application.port.out.ConsultaAeroportosPort;
import com.bcopstein.voos.application.port.out.ConsultaAeroviasPort;
import com.bcopstein.voos.application.port.out.ConsultaPilotosPort;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.SituacaoPlano;
import com.bcopstein.voos.domain.model.plano.TipoPassagem;
import com.bcopstein.voos.domain.model.plano.Trecho;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.stereotype.Repository;

/**
 * Converte o agregado PlanoDeVoo (domínio) para as tabelas planos_de_voo e trechos, e vice-versa.
 * As referências (piloto, aeronave, aeroportos, aerovias) são gravadas por identificador.
 */
@Repository
public class PlanoDeVooJpaAdapter implements PlanoDeVooPort {
    private final PlanoDeVooJpaRepository repository;
    private final ConsultaPilotosPort pilotos;
    private final ConsultaAeronavesPort aeronaves;
    private final ConsultaAeroportosPort aeroportos;
    private final ConsultaAeroviasPort aerovias;

    public PlanoDeVooJpaAdapter(PlanoDeVooJpaRepository repository, ConsultaPilotosPort pilotos,
                                ConsultaAeronavesPort aeronaves, ConsultaAeroportosPort aeroportos,
                                ConsultaAeroviasPort aerovias) {
        this.repository = repository;
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
        this.aeroportos = aeroportos;
        this.aerovias = aerovias;
    }

    @Override
    public Optional<PlanoDeVoo> buscar(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<PlanoDeVoo> listar() {
        return repository.findAllByOrderByIdAsc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<PlanoDeVoo> listarPorSituacao(SituacaoPlano situacao) {
        return repository.findBySituacaoOrderByIdAsc(situacao.name()).stream().map(this::toDomain).toList();
    }

    @Override
    public PlanoDeVoo salvar(PlanoDeVoo plano) {
        PlanoDeVooEntity entity = plano.getId() == null
                ? new PlanoDeVooEntity(null)
                : repository.findById(plano.getId()).orElseThrow(
                        () -> new IllegalStateException("Plano " + plano.getId() + " não existe para atualização."));
        entity.atualizar(plano.getPiloto().getId(), plano.getAeronave().getPrefixo(), plano.getOrigem().getCodigo(),
                plano.getDestino().getCodigo(), plano.getPartidaPrevista(), plano.getSituacao().name(),
                plano.getTrechos().stream().map(PlanoDeVooJpaAdapter::toEmbeddable).toList());
        return toDomain(repository.saveAndFlush(entity));
    }

    private static TrechoEmbeddable toEmbeddable(Trecho t) {
        return new TrechoEmbeddable(t.getAerovia().getId(), t.getAltitude(), t.getEntradaPrevista(), t.getChegadaPrevista(),
                t.getTipoPassagem().name(), t.getPermanenciaMinutos(), t.getVagaPretendida());
    }

    private PlanoDeVoo toDomain(PlanoDeVooEntity e) {
        Piloto piloto = exigir(pilotos.buscar(e.getPilotoId()), () -> "piloto " + e.getPilotoId());
        Aeronave aeronave = exigir(aeronaves.buscar(e.getAeronavePrefixo()), () -> "aeronave " + e.getAeronavePrefixo());
        Aeroporto origem = exigir(aeroportos.buscar(e.getOrigemCodigo()), () -> "aeroporto " + e.getOrigemCodigo());
        Aeroporto destino = exigir(aeroportos.buscar(e.getDestinoCodigo()), () -> "aeroporto " + e.getDestinoCodigo());
        List<Trecho> trechos = e.getTrechos().stream().map(t -> {
            Aerovia aerovia = exigir(aerovias.buscar(t.getAeroviaId()), () -> "aerovia " + t.getAeroviaId());
            return Trecho.reconstituir(aerovia, t.getAltitude(), t.getEntradaPrevista(), t.getChegadaPrevista(),
                    TipoPassagem.valueOf(t.getTipoPassagem()), t.getPermanenciaMinutos(), t.getVagaPretendida());
        }).toList();
        return PlanoDeVoo.reconstituir(e.getId(), piloto, aeronave, origem, destino, e.getPartidaPrevista(),
                SituacaoPlano.valueOf(e.getSituacao()), trechos);
    }

    private static <T> T exigir(Optional<T> valor, Supplier<String> descricao) {
        return valor.orElseThrow(() -> new IllegalStateException("Plano referencia " + descricao.get() + " inexistente."));
    }
}
