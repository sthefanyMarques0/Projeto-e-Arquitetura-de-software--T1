package com.bcopstein.voos.application.usecase.cadastro;

import com.bcopstein.voos.application.port.in.ConsultarAeronaves;
import com.bcopstein.voos.application.port.out.ConsultaAeronavesPort;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConsultarAeronavesUseCase implements ConsultarAeronaves {
    private final ConsultaAeronavesPort consulta;

    public ConsultarAeronavesUseCase(ConsultaAeronavesPort consulta) {
        this.consulta = consulta;
    }

    @Override
    public List<Aeronave> consultar() { return consulta.listar(); }
}
