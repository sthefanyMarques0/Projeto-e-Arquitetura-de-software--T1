package com.bcopstein.voos.servico;

import com.bcopstein.voos.dominio.*;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ServicoCalculoHorarios {

    public Duration calcularDuracaoTrecho(Aerovia aerovia, Aeronave aeronave) {
        return aerovia.getDuracao(aeronave);
    }

    public LocalDateTime calcularChegada(Trecho trecho, Aeronave aeronave) {
        Duration duracao = calcularDuracaoTrecho(trecho.getAerovia(), aeronave);
        return trecho.getEntradaPrevista().plus(duracao);
    }
}
