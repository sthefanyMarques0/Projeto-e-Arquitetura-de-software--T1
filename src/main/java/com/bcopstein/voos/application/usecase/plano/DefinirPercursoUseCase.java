package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.port.in.DefinirPercurso;
import com.bcopstein.voos.application.port.in.TrechoCommand;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.Trecho;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Define (ou redefine) o percurso inteiro: adicionar, remover e reordenar trechos. Não reserva recursos. */
@Service
public class DefinirPercursoUseCase implements DefinirPercurso {
    private final ReferenciasDoPlano referencias;
    private final PlanoDeVooPort planos;

    public DefinirPercursoUseCase(ReferenciasDoPlano referencias, PlanoDeVooPort planos) {
        this.referencias = referencias;
        this.planos = planos;
    }

    @Override
    @Transactional
    public PlanoDeVoo definir(Long planoId, List<TrechoCommand> comandos) {
        PlanoDeVoo plano = referencias.plano(planoId);
        plano.garantirEmEdicao("alterado");
        List<Trecho> trechos = comandos.stream()
                .map(c -> Trecho.criar(referencias.aerovia(c.aeroviaId()), c.altitude(), c.entradaPrevista(),
                        c.tipoPassagem(), c.permanenciaMinutos(), c.vagaPretendida(), plano.getAeronave()))
                .toList();
        plano.definirPercurso(trechos);
        return planos.salvar(plano);
    }
}
