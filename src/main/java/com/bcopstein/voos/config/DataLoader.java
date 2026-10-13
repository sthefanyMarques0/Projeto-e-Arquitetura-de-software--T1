package com.bcopstein.voos.config;

import com.bcopstein.voos.dominio.*;
import com.bcopstein.voos.repositorio.*;
import com.bcopstein.voos.servico.ServicoAprovacaoPlano;
import com.bcopstein.voos.servico.ServicoCalculoHorarios;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {
    private final AeroportoRepository aeroportoRepo;
    private final AeroviaRepository aeroviaRepo;
    private final PilotoRepository pilotoRepo;
    private final AeronaveRepository aeronaveRepo;
    private final PlanoDeVooRepository planoRepo;
    private final ServicoCalculoHorarios servicoCalculo;
    private final ServicoAprovacaoPlano servicoAprovacao;

    public static final LocalDate DATA_REFERENCIA = LocalDate.of(2026, 11, 20);

    public DataLoader(AeroportoRepository apr, AeroviaRepository avr, PilotoRepository plr,
                      AeronaveRepository anr, PlanoDeVooRepository pr,
                      ServicoCalculoHorarios sc, ServicoAprovacaoPlano sa) {
        this.aeroportoRepo = apr;
        this.aeroviaRepo = avr;
        this.pilotoRepo = plr;
        this.aeronaveRepo = anr;
        this.planoRepo = pr;
        this.servicoCalculo = sc;
        this.servicoAprovacao = sa;
    }

    @Override
    public void run(String... args) {
        if (aeroportoRepo.count() > 0) return;

        // 1. Aeroportos Iniciais (Tabela da Secao 2)
        Aeroporto poa = aeroportoRepo.save(new Aeroporto("POA", "Aeroporto Internacional Salgado Filho --- Porto Alegre", 41));
        Aeroporto fln = aeroportoRepo.save(new Aeroporto("FLN", "Aeroporto Internacional de Florianópolis/Hercílio Luz", 16));
        Aeroporto cwb = aeroportoRepo.save(new Aeroporto("CWB", "Aeroporto Internacional Afonso Pena --- Curitiba", 26));
        Aeroporto gru = aeroportoRepo.save(new Aeroporto("GRU", "Aeroporto Internacional de São Paulo/Guarulhos", 115));

        // 2. Aerovias Iniciais R1 a R8 (Tabela da Secao 2)
        Aerovia r1 = aeroviaRepo.save(new Aerovia("R1", poa, fln, 363));
        Aerovia r2 = aeroviaRepo.save(new Aerovia("R2", poa, gru, 865));
        Aerovia r3 = aeroviaRepo.save(new Aerovia("R3", gru, poa, 865));
        Aerovia r4 = aeroviaRepo.save(new Aerovia("R4", fln, cwb, 245));
        Aerovia r5 = aeroviaRepo.save(new Aerovia("R5", cwb, fln, 245));
        Aerovia r6 = aeroviaRepo.save(new Aerovia("R6", cwb, gru, 360));
        Aerovia r7 = aeroviaRepo.save(new Aerovia("R7", gru, cwb, 360));
        Aerovia r8 = aeroviaRepo.save(new Aerovia("R8", fln, poa, 363));

        // 3. Pilotos
        Piloto p1 = pilotoRepo.save(new Piloto(101L, "Cmte. Carlos Silva"));
        Piloto p2 = pilotoRepo.save(new Piloto(102L, "Cmte. Beatriz Santos"));
        Piloto p3 = pilotoRepo.save(new Piloto(103L, "Cmte. Daniel Oliveira"));
        Piloto p4 = pilotoRepo.save(new Piloto(104L, "Cmte. Mariana Souza"));

        // 4. Aeronaves das 3 categorias (Secao 3)
        Aeronave anParticular = new Aeronave("PR-EXE", 600.0, 2200.0, CategoriaAeronave.PARTICULAR);
        anParticular.setEmpresaManutencao("Helisul Aviação");
        aeronaveRepo.save(anParticular);

        Aeronave anPassageiros1 = new Aeronave("PT-AZU", 850.0, 4200.0, CategoriaAeronave.COMERCIAL_PASSAGEIROS);
        anPassageiros1.setCompanhiaAerea("Azul Linhas Aéreas");
        anPassageiros1.setCapacidadePassageiros(174);
        aeronaveRepo.save(anPassageiros1);

        Aeronave anPassageiros2 = new Aeronave("PP-GLO", 850.0, 4500.0, CategoriaAeronave.COMERCIAL_PASSAGEIROS);
        anPassageiros2.setCompanhiaAerea("Gol Transportes Aéreos");
        anPassageiros2.setCapacidadePassageiros(186);
        aeronaveRepo.save(anPassageiros2);

        Aeronave anCarga = new Aeronave("PR-CRG", 750.0, 5000.0, CategoriaAeronave.COMERCIAL_CARGA);
        anCarga.setCompanhiaAerea("Total Cargo");
        anCarga.setCapacidadeCargaTon(35.0);
        aeronaveRepo.save(anCarga);

        // 5. Carga de Planos Aprovados na DATA DE REFERÊNCIA (2026-11-20)
        // Ocupando pelo menos 5 aerovias (R1, R4, R6, R3, R5 = 62.5% da malha inicial)

        // Plano 1: POA -> FLN (R1) -> CWB (R4) [Ocupa R1 e R4]
        LocalDateTime d1 = DATA_REFERENCIA.atTime(8, 0);
        PlanoDeVoo plano1 = new PlanoDeVoo(d1, p1, anPassageiros1, poa, cwb);
        Trecho t1_1 = new Trecho(r1, 29000, d1, TipoPassagem.SOBREVOO, 0, 0);
        t1_1.setChegadaPrevista(servicoCalculo.calcularChegada(t1_1, anPassageiros1));
        plano1.adicionarTrecho(t1_1);

        Trecho t1_2 = new Trecho(r4, 29000, t1_1.getChegadaPrevista(), TipoPassagem.POUSO, 60, 1);
        t1_2.setChegadaPrevista(servicoCalculo.calcularChegada(t1_2, anPassageiros1));
        plano1.adicionarTrecho(t1_2);
        planoRepo.save(plano1);
        servicoAprovacao.aprovar(plano1, LocalDateTime.now());

        // Plano 2: CWB -> GRU (R6) [Ocupa R6]
        LocalDateTime d2 = DATA_REFERENCIA.atTime(10, 0);
        PlanoDeVoo plano2 = new PlanoDeVoo(d2, p2, anPassageiros2, cwb, gru);
        Trecho t2_1 = new Trecho(r6, 31000, d2, TipoPassagem.POUSO, 120, 2);
        t2_1.setChegadaPrevista(servicoCalculo.calcularChegada(t2_1, anPassageiros2));
        plano2.adicionarTrecho(t2_1);
        planoRepo.save(plano2);
        servicoAprovacao.aprovar(plano2, LocalDateTime.now());

        // Plano 3: Carga GRU -> POA (R3) [Ocupa R3 na janela de carga 00:00 - 06:00]
        LocalDateTime d3 = DATA_REFERENCIA.atTime(2, 0);
        PlanoDeVoo plano3 = new PlanoDeVoo(d3, p3, anCarga, gru, poa);
        Trecho t3_1 = new Trecho(r3, 28000, d3, TipoPassagem.POUSO, 90, 5);
        t3_1.setChegadaPrevista(servicoCalculo.calcularChegada(t3_1, anCarga));
        plano3.adicionarTrecho(t3_1);
        planoRepo.save(plano3);
        servicoAprovacao.aprovar(plano3, LocalDateTime.now());

        // Plano 4: Particular CWB -> FLN (R5) [Ocupa R5 na altitude 26.000 pés]
        LocalDateTime d4 = DATA_REFERENCIA.atTime(14, 0);
        PlanoDeVoo plano4 = new PlanoDeVoo(d4, p4, anParticular, cwb, fln);
        Trecho t4_1 = new Trecho(r5, 26000, d4, TipoPassagem.POUSO, 45, 3);
        t4_1.setChegadaPrevista(servicoCalculo.calcularChegada(t4_1, anParticular));
        plano4.adicionarTrecho(t4_1);
        planoRepo.save(plano4);
        servicoAprovacao.aprovar(plano4, LocalDateTime.now());

        System.out.println(">>> DataLoader finalizado com sucesso!");
        System.out.println(">>> Data de Referência: " + DATA_REFERENCIA);
        System.out.println(">>> Aerovias ocupadas: R1, R3, R4, R5, R6 (5/8 = 62.5% da malha inicial)");
    }
}
