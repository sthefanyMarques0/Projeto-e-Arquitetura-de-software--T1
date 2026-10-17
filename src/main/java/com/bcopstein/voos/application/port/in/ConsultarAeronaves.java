package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import java.util.List;

public interface ConsultarAeronaves {
    List<Aeronave> consultar();
}
