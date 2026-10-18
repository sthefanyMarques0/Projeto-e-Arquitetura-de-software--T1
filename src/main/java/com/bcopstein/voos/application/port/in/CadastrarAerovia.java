package com.bcopstein.voos.application.port.in;

import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.math.BigDecimal;

public interface CadastrarAerovia {
    Aerovia cadastrar(String id, String origem, String destino, BigDecimal distanciaKm);
}
