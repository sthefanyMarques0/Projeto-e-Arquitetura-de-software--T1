package com.bcopstein.voos.application.port.out;

import java.time.LocalDateTime;

/** Fornece o instante atual no fuso operacional (America/Sao_Paulo). */
public interface RelogioPort {
    LocalDateTime agora();
}
