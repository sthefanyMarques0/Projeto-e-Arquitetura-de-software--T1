package com.bcopstein.voos.application.usecase.cadastro;

import com.bcopstein.voos.application.port.in.ConsultarAerovias;
import com.bcopstein.voos.application.port.out.ConsultaAeroviasPort;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConsultarAeroviasUseCase implements ConsultarAerovias {
    private final ConsultaAeroviasPort consulta;

    public ConsultarAeroviasUseCase(ConsultaAeroviasPort consulta) {
        this.consulta = consulta;
    }

    @Override
    public List<Aerovia> consultar() { return consulta.listar(); }
}
