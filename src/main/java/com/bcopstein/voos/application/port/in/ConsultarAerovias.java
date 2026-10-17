package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.util.List;

public interface ConsultarAerovias {
    List<Aerovia> consultar();
}
