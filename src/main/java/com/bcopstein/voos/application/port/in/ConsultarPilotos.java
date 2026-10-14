package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.piloto.Piloto;
import java.util.List;

public interface ConsultarPilotos {
    List<Piloto> consultar();
}
