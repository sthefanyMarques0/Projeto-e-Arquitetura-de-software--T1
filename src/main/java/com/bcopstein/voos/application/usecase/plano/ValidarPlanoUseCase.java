package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.port.in.ValidarPlano;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.ResultadoValidacao;
import com.bcopstein.voos.domain.service.ServicoValidacaoPlano;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Valida sem alterar o estado do plano e sem reservar recursos. */
@Service
public class ValidarPlanoUseCase implements ValidarPlano {
    private final ReferenciasDoPlano referencias;
    private final ReservasVigentes reservasVigentes;
    private final ServicoValidacaoPlano servicoValidacao;

    public ValidarPlanoUseCase(ReferenciasDoPlano referencias, ReservasVigentes reservasVigentes,
                               ServicoValidacaoPlano servicoValidacao) {
        this.referencias = referencias;
        this.reservasVigentes = reservasVigentes;
        this.servicoValidacao = servicoValidacao;
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoValidacao validar(Long planoId) {
        PlanoDeVoo plano = referencias.plano(planoId);
        plano.garantirEmEdicao("validado");
        return servicoValidacao.validar(plano, reservasVigentes.paraPlano(plano));
    }
}
