package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.exception.PlanoInvalidoException;
import com.bcopstein.voos.application.port.in.AprovarPlano;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.application.port.out.RelogioPort;
import com.bcopstein.voos.application.port.out.ReservaPort;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.ResultadoValidacao;
import com.bcopstein.voos.domain.service.ServicoReservas;
import com.bcopstein.voos.domain.service.ServicoValidacaoPlano;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aprovação integral ("tudo ou nada"), numa única transação:
 * revalida no instante atual, grava todas as reservas e muda o plano para APROVADO.
 * Qualquer falha desfaz a transação: nenhuma reserva fica e o plano continua EM_EDICAO.
 * A restrição de unicidade do banco impede reserva dupla mesmo com aprovações concorrentes.
 */
@Service
public class AprovarPlanoUseCase implements AprovarPlano {
    private final ReferenciasDoPlano referencias;
    private final ReservasVigentes reservasVigentes;
    private final PlanoDeVooPort planos;
    private final ReservaPort reservas;
    private final RelogioPort relogio;
    private final ServicoValidacaoPlano servicoValidacao;
    private final ServicoReservas servicoReservas;

    public AprovarPlanoUseCase(ReferenciasDoPlano referencias, ReservasVigentes reservasVigentes, PlanoDeVooPort planos,
                               ReservaPort reservas, RelogioPort relogio, ServicoValidacaoPlano servicoValidacao,
                               ServicoReservas servicoReservas) {
        this.referencias = referencias;
        this.reservasVigentes = reservasVigentes;
        this.planos = planos;
        this.reservas = reservas;
        this.relogio = relogio;
        this.servicoValidacao = servicoValidacao;
        this.servicoReservas = servicoReservas;
    }

    @Override
    @Transactional
    public PlanoDeVoo aprovar(Long planoId) {
        PlanoDeVoo plano = referencias.plano(planoId);
        plano.garantirEmEdicao("aprovado");

        LocalDateTime agora = relogio.agora();
        if (!plano.partidaEhPosteriorA(agora)) {
            throw new OperacaoNaoPermitidaException("A aprovação exige partida estritamente futura. Partida prevista: "
                    + plano.getPartidaPrevista() + "; agora: " + agora + ".");
        }

        ResultadoValidacao resultado = servicoValidacao.validar(plano, reservasVigentes.paraPlano(plano));
        if (!resultado.isValido()) {
            throw new PlanoInvalidoException(resultado.getErros());
        }

        reservas.registrar(servicoReservas.reservasNecessarias(plano));
        plano.aprovar();
        return planos.salvar(plano);
    }
}
