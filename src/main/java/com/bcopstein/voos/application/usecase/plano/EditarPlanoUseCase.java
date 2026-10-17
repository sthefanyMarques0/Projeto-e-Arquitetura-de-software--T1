package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.port.in.DadosPlanoCommand;
import com.bcopstein.voos.application.port.in.EditarPlano;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditarPlanoUseCase implements EditarPlano {
    private final ReferenciasDoPlano referencias;
    private final PlanoDeVooPort planos;

    public EditarPlanoUseCase(ReferenciasDoPlano referencias, PlanoDeVooPort planos) {
        this.referencias = referencias;
        this.planos = planos;
    }

    @Override
    @Transactional
    public PlanoDeVoo editar(Long planoId, DadosPlanoCommand dados) {
        PlanoDeVoo plano = referencias.plano(planoId);
        plano.garantirEmEdicao("alterado");
        plano.alterarDados(
                referencias.piloto(dados.pilotoId()),
                referencias.aeronave(dados.prefixoAeronave()),
                referencias.aeroporto(dados.origem()),
                referencias.aeroporto(dados.destino()),
                dados.partidaPrevista());
        return planos.salvar(plano);
    }
}
