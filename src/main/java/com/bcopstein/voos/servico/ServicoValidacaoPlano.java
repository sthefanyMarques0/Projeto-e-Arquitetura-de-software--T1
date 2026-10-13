package com.bcopstein.voos.servico;

import com.bcopstein.voos.dominio.*;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ServicoValidacaoPlano {
    private final ServicoCalculoHorarios servicoCalculoHorarios;
    private final ServicoAutonomia servicoAutonomia;
    private final ServicoDisponibilidade servicoDisponibilidade;

    public ServicoValidacaoPlano(ServicoCalculoHorarios sc, ServicoAutonomia sa, ServicoDisponibilidade sd) {
        this.servicoCalculoHorarios = sc;
        this.servicoAutonomia = sa;
        this.servicoDisponibilidade = sd;
    }

    public ResultadoValidacao validar(PlanoDeVoo plano) {
        ResultadoValidacao res = new ResultadoValidacao();

        if (plano.getTrechos() == null || plano.getTrechos().isEmpty()) {
            res.adicionarErro("O plano de voo deve conter ao menos um trecho.");
            return res;
        }

        Trecho primeiroTrecho = plano.getTrechos().get(0);
        Trecho ultimoTrecho = plano.getTrechos().get(plano.getTrechos().size() - 1);

        // 1. Origem e Destino do Plano
        if (!primeiroTrecho.getAerovia().getOrigem().getCodigo().equalsIgnoreCase(plano.getOrigem().getCodigo())) {
            res.adicionarErro("A origem da primeira aerovia (" + primeiroTrecho.getAerovia().getOrigem().getCodigo()
                    + ") difere da origem do plano (" + plano.getOrigem().getCodigo() + ").");
        }
        if (!ultimoTrecho.getAerovia().getDestino().getCodigo().equalsIgnoreCase(plano.getDestino().getCodigo())) {
            res.adicionarErro("O destino da última aerovia (" + ultimoTrecho.getAerovia().getDestino().getCodigo()
                    + ") difere do destino do plano (" + plano.getDestino().getCodigo() + ").");
        }

        // 2. Horario de partida do plano == entrada do primeiro trecho
        if (!primeiroTrecho.getEntradaPrevista().isEqual(plano.getPartidaPrevista())) {
            res.adicionarErro("O horário de entrada no primeiro trecho deve corresponder ao horário de partida do plano.");
        }

        // 3. Pouso obrigatorio no destino final
        if (ultimoTrecho.getTipoPassagem() != TipoPassagem.POUSO) {
            res.adicionarErro("No destino final deve ocorrer obrigatoriamente um pouso com tempo de permanência.");
        }

        // 4. Limite diario: partida e chegada final no mesmo dia
        if (!plano.getPartidaPrevista().toLocalDate().isEqual(ultimoTrecho.getChegadaPrevista().toLocalDate())) {
            res.adicionarErro("O voo deve iniciar e terminar no mesmo dia (chegada na mesma data da partida).");
        }

        // 5. Continuidade geografica, cronologica e calculo de horarios
        double distanciaAcumuladaTrecho = 0;
        for (int i = 0; i < plano.getTrechos().size(); i++) {
            Trecho atual = plano.getTrechos().get(i);
            distanciaAcumuladaTrecho += atual.getAerovia().getDistanciaKm();

            // Verificacao de duracao e chegada calculada
            LocalDateTime chegadaEsperada = servicoCalculoHorarios.calcularChegada(atual, plano.getAeronave());
            if (!atual.getChegadaPrevista().isEqual(chegadaEsperada)) {
                res.adicionarErro("Chegada do trecho " + (i + 1) + " incompatível com a velocidade da aeronave.");
            }

            // Regras de operacao da aeronave (altitude / horario carga)
            if (!plano.getAeronave().validarRegrasOperacao(atual.getAltitude(), atual.getEntradaPrevista(), atual.getChegadaPrevista())) {
                res.adicionarErro("Aeronave " + plano.getAeronave().getPrefixo() + " (" + plano.getAeronave().getCategoria()
                        + ") não autorizada para altitude " + atual.getAltitude() + " ou janela de horário.");
            }

            // Disponibilidade da altitude
            Periodo periodoVoo = new Periodo(atual.getEntradaPrevista(), atual.getChegadaPrevista());
            if (!servicoDisponibilidade.verificarAltitude(atual.getAerovia(), atual.getAltitude(), periodoVoo)) {
                res.adicionarErro("Altitude " + atual.getAltitude() + " indisponível na aerovia " + atual.getAerovia().getId());
            }

            // Se houve pouso: verificar disponibilidade de vaga e reiniciar acumulador de autonomia
            if (atual.getTipoPassagem() == TipoPassagem.POUSO) {
                if (distanciaAcumuladaTrecho > plano.getAeronave().getAutonomiaSegura()) {
                    res.adicionarErro("Distância (" + distanciaAcumuladaTrecho + " km) excede autonomia segura ("
                            + plano.getAeronave().getAutonomiaSegura() + " km) até pouso no trecho " + (i + 1));
                }
                distanciaAcumuladaTrecho = 0; // Reabastecido no pouso

                LocalDateTime saidaVaga = atual.getChegadaPrevista().plusMinutes(atual.getTempoPermanencia());
                Periodo periodoVaga = new Periodo(atual.getChegadaPrevista(), saidaVaga);
                if (!servicoDisponibilidade.verificarVaga(atual.getAerovia().getDestino(), atual.getVagaPretendida(), periodoVaga)) {
                    res.adicionarErro("Vaga " + atual.getVagaPretendida() + " indisponível no aeroporto "
                            + atual.getAerovia().getDestino().getCodigo());
                }
            }

            // Continuidade com proximo trecho
            if (i < plano.getTrechos().size() - 1) {
                Trecho proximo = plano.getTrechos().get(i + 1);
                if (!atual.getAerovia().getDestino().getCodigo().equalsIgnoreCase(proximo.getAerovia().getOrigem().getCodigo())) {
                    res.adicionarErro("Descontinuidade geográfica entre o destino de " + atual.getAerovia().getId()
                            + " e a origem de " + proximo.getAerovia().getId());
                }

                if (atual.getTipoPassagem() == TipoPassagem.SOBREVOO) {
                    if (!proximo.getEntradaPrevista().isEqual(atual.getChegadaPrevista())) {
                        res.adicionarErro("Após sobrevoo, entrada no próximo trecho deve ser igual à chegada anterior.");
                    }
                } else if (atual.getTipoPassagem() == TipoPassagem.POUSO) {
                    LocalDateTime entradaEsperada = atual.getChegadaPrevista().plusMinutes(atual.getTempoPermanencia());
                    if (!proximo.getEntradaPrevista().isEqual(entradaEsperada)) {
                        res.adicionarErro("Após pouso, entrada no próximo trecho deve ser igual à chegada + permanência.");
                    }
                }
            }
        }

        return res;
    }
}
