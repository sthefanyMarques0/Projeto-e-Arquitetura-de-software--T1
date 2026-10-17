package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.port.in.CriarPlano;
import com.bcopstein.voos.application.port.in.DadosPlanoCommand;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarPlanoUseCase implements CriarPlano {
    private final ReferenciasDoPlano referencias;
    private final PlanoDeVooPort planos;

    public CriarPlanoUseCase(ReferenciasDoPlano referencias, PlanoDeVooPort planos) {
        this.referencias = referencias;
        this.planos = planos;
    }

    @Override
    @Transactional
    public PlanoDeVoo criar(DadosPlanoCommand dados) {
        PlanoDeVoo plano = PlanoDeVoo.criar(
                referencias.piloto(dados.pilotoId()),
                referencias.aeronave(dados.prefixoAeronave()),
                referencias.aeroporto(dados.origem()),
                referencias.aeroporto(dados.destino()),
                dados.partidaPrevista());
        return planos.salvar(plano);
    }
}
