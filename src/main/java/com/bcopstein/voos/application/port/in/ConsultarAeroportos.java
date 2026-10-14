package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.rede.Aeroporto;
import java.util.List;

public interface ConsultarAeroportos {
    List<Aeroporto> consultar();
}
