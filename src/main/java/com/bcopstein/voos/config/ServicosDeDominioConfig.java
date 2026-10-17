package com.bcopstein.voos.config;

import com.bcopstein.voos.domain.service.ServicoAutonomia;
import com.bcopstein.voos.domain.service.ServicoDisponibilidade;
import com.bcopstein.voos.domain.service.ServicoReservas;
import com.bcopstein.voos.domain.service.ServicoValidacaoPlano;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registra os serviços de domínio como beans, mantendo o pacote domain livre de anotações do Spring. */
@Configuration
public class ServicosDeDominioConfig {

    @Bean
    public ServicoAutonomia servicoAutonomia() {
        return new ServicoAutonomia();
    }

    @Bean
    public ServicoDisponibilidade servicoDisponibilidade() {
        return new ServicoDisponibilidade();
    }

    @Bean
    public ServicoReservas servicoReservas() {
        return new ServicoReservas();
    }

    @Bean
    public ServicoValidacaoPlano servicoValidacaoPlano(ServicoAutonomia autonomia, ServicoDisponibilidade disponibilidade,
                                                       ServicoReservas reservas) {
        return new ServicoValidacaoPlano(autonomia, disponibilidade, reservas);
    }
}
