package com.bcopstein.voos.application.usecase.cadastro;

import com.bcopstein.voos.application.port.in.ConsultarPilotos;
import com.bcopstein.voos.application.port.out.ConsultaPilotosPort;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConsultarPilotosUseCase implements ConsultarPilotos {
    private final ConsultaPilotosPort consulta;

    public ConsultarPilotosUseCase(ConsultaPilotosPort consulta) {
        this.consulta = consulta;
    }

    @Override
    public List<Piloto> consultar() { return consulta.listar(); }
}
