package com.bcopstein.voos.adapter.in.rest.mapper;

import com.bcopstein.voos.adapter.in.rest.dto.PlanoRequest;
import com.bcopstein.voos.adapter.in.rest.dto.PlanoResponse;
import com.bcopstein.voos.adapter.in.rest.dto.ReservaResponse;
import com.bcopstein.voos.adapter.in.rest.dto.TrechoRequest;
import com.bcopstein.voos.adapter.in.rest.dto.TrechoResponse;
import com.bcopstein.voos.adapter.in.rest.dto.ValidacaoResponse;
import com.bcopstein.voos.application.port.in.DadosPlanoCommand;
import com.bcopstein.voos.application.port.in.TrechoCommand;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.ResultadoValidacao;
import com.bcopstein.voos.domain.model.plano.Trecho;
import com.bcopstein.voos.domain.model.reserva.Reserva;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class PlanoRestMapper {
    private PlanoRestMapper() {}

    public static DadosPlanoCommand toCommand(PlanoRequest r) {
        return new DadosPlanoCommand(r.pilotoId(), r.prefixoAeronave(), r.origem(), r.destino(), r.partidaPrevista());
    }

    public static TrechoCommand toCommand(TrechoRequest r) {
        return new TrechoCommand(r.aeroviaId(), r.altitude(), r.entradaPrevista(), r.tipoPassagem(),
                r.permanenciaMinutos(), r.vagaPretendida());
    }

    public static PlanoResponse toResponse(PlanoDeVoo p) {
        List<TrechoResponse> trechos = new ArrayList<>();
        List<Trecho> lista = p.getTrechos();
        for (int i = 0; i < lista.size(); i++) {
            Trecho t = lista.get(i);
            trechos.add(new TrechoResponse(i + 1, t.getAerovia().getId(), t.getAerovia().getOrigem().getCodigo(),
                    t.getAerovia().getDestino().getCodigo(), t.getAerovia().getDistanciaKm(), t.getAltitude(),
                    t.getEntradaPrevista(), t.getChegadaPrevista(),
                    Duration.between(t.getEntradaPrevista(), t.getChegadaPrevista()).getSeconds(),
                    t.getTipoPassagem().name(), t.getPermanenciaMinutos(), t.getVagaPretendida()));
        }
        return new PlanoResponse(p.getId(), p.getSituacao().name(), p.getPiloto().getId(), p.getPiloto().getNome(),
                p.getAeronave().getPrefixo(), p.getAeronave().getCategoria().name(), p.getOrigem().getCodigo(),
                p.getDestino().getCodigo(), p.getPartidaPrevista(), p.getChegadaFinal().orElse(null), trechos);
    }

    public static ValidacaoResponse toResponse(Long planoId, ResultadoValidacao r) {
        return new ValidacaoResponse(planoId, r.isValido(), r.getErros());
    }

    public static ReservaResponse toResponse(Reserva r) {
        return new ReservaResponse(r.getId(), r.getPlanoId(), r.getTipo().name(), r.getAeroviaId(), r.getAltitude(),
                r.getAeroportoCodigo(), r.getVaga(), r.getSlot().inicio(), r.getSlot().fim());
    }
}
