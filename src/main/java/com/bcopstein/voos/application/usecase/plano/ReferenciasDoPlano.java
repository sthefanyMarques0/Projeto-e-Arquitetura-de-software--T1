package com.bcopstein.voos.application.usecase.plano;

import com.bcopstein.voos.application.exception.RecursoNaoEncontradoException;
import com.bcopstein.voos.application.port.out.ConsultaAeronavesPort;
import com.bcopstein.voos.application.port.out.ConsultaAeroportosPort;
import com.bcopstein.voos.application.port.out.ConsultaAeroviasPort;
import com.bcopstein.voos.application.port.out.ConsultaPilotosPort;
import com.bcopstein.voos.application.port.out.PlanoDeVooPort;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import org.springframework.stereotype.Component;

/** Carrega, pelas portas de saída, os objetos referenciados nas requisições (404 se não existirem). */
@Component
public class ReferenciasDoPlano {
    private final PlanoDeVooPort planos;
    private final ConsultaPilotosPort pilotos;
    private final ConsultaAeronavesPort aeronaves;
    private final ConsultaAeroportosPort aeroportos;
    private final ConsultaAeroviasPort aerovias;

    public ReferenciasDoPlano(PlanoDeVooPort planos, ConsultaPilotosPort pilotos, ConsultaAeronavesPort aeronaves,
                              ConsultaAeroportosPort aeroportos, ConsultaAeroviasPort aerovias) {
        this.planos = planos;
        this.pilotos = pilotos;
        this.aeronaves = aeronaves;
        this.aeroportos = aeroportos;
        this.aerovias = aerovias;
    }

    public PlanoDeVoo plano(Long id) {
        return planos.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Plano de voo " + id + " não encontrado."));
    }

    public Piloto piloto(Long id) {
        return pilotos.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Piloto " + id + " não encontrado."));
    }

    public Aeronave aeronave(String prefixo) {
        return aeronaves.buscar(prefixo).orElseThrow(() -> new RecursoNaoEncontradoException("Aeronave " + prefixo + " não encontrada."));
    }

    public Aeroporto aeroporto(String codigo) {
        return aeroportos.buscar(codigo).orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto " + codigo + " não encontrado."));
    }

    public Aerovia aerovia(String id) {
        return aerovias.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Aerovia " + id + " não encontrada."));
    }
}
