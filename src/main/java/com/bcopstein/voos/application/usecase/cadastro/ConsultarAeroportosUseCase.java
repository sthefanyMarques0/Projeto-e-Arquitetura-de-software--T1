package com.bcopstein.voos.application.usecase.cadastro;

import com.bcopstein.voos.application.port.in.ConsultarAeroportos;
import com.bcopstein.voos.application.port.out.ConsultaAeroportosPort;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConsultarAeroportosUseCase implements ConsultarAeroportos {
    private final ConsultaAeroportosPort consulta;

    public ConsultarAeroportosUseCase(ConsultaAeroportosPort consulta) {
        this.consulta = consulta;
    }

    @Override
    public List<Aeroporto> consultar() { return consulta.listar(); }
}
