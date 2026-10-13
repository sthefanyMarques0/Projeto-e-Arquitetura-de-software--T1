package com.bcopstein.voos.servico;

import com.bcopstein.voos.dominio.*;
import com.bcopstein.voos.repositorio.PlanoDeVooRepository;
import com.bcopstein.voos.repositorio.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ServicoAprovacaoPlano {
    private final PlanoDeVooRepository planoRepo;
    private final ReservaRepository reservaRepo;
    private final ServicoValidacaoPlano validacaoService;

    public ServicoAprovacaoPlano(PlanoDeVooRepository pr, ReservaRepository rr, ServicoValidacaoPlano vs) {
        this.planoRepo = pr;
        this.reservaRepo = rr;
        this.validacaoService = vs;
    }

    @Transactional
    public PlanoDeVoo aprovar(PlanoDeVoo plano, LocalDateTime momento) {
        if (plano.getSituacao() != SituacaoPlano.EM_EDICAO) {
            throw new IllegalStateException("Apenas planos em edição podem ser aprovados.");
        }

        ResultadoValidacao val = validacaoService.validar(plano);
        if (!val.isValido()) {
            throw new IllegalArgumentException("Plano inválido para aprovação: " + String.join("; ", val.getErros()));
        }

        // Criacao atômica de reservas integrais
        for (Trecho t : plano.getTrechos()) {
            Periodo periodoVoo = new Periodo(t.getEntradaPrevista(), t.getChegadaPrevista());
            for (Slot slot : periodoVoo.getSlots()) {
                reservaRepo.save(Reserva.criarReservaAerovia(t.getAerovia(), t.getAltitude(), slot, plano));
            }

            if (t.getTipoPassagem() == TipoPassagem.POUSO) {
                LocalDateTime saidaVaga = t.getChegadaPrevista().plusMinutes(t.getTempoPermanencia());
                Periodo periodoVaga = new Periodo(t.getChegadaPrevista(), saidaVaga);
                for (Slot slot : periodoVaga.getSlots()) {
                    reservaRepo.save(Reserva.criarReservaAeroporto(t.getAerovia().getDestino(), t.getVagaPretendida(), slot, plano));
                }
            }
        }

        plano.setSituacao(SituacaoPlano.APROVADO);
        return planoRepo.save(plano);
    }

    @Transactional
    public void cancelar(PlanoDeVoo plano, LocalDateTime momento) {
        if (plano.getSituacao() != SituacaoPlano.APROVADO) {
            throw new IllegalStateException("Apenas planos aprovados podem ser cancelados.");
        }
        if (!plano.getPartidaPrevista().isAfter(momento)) {
            throw new IllegalStateException("Voo com partida já iniciada/ocorrida não pode ser cancelado (considerado executado).");
        }

        reservaRepo.deleteByPlano(plano);
        plano.setSituacao(SituacaoPlano.CANCELADO);
        planoRepo.save(plano);
    }

    @Transactional
    public void abandonar(PlanoDeVoo plano) {
        if (plano.getSituacao() != SituacaoPlano.EM_EDICAO) {
            throw new IllegalStateException("Apenas planos em edição podem ser abandonados.");
        }
        plano.setSituacao(SituacaoPlano.ABANDONADO);
        planoRepo.save(plano);
    }
}
