package com.bcopstein.voos.domain;

import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.aeronave.AeronaveCarga;
import com.bcopstein.voos.domain.model.aeronave.AeronaveParticular;
import com.bcopstein.voos.domain.model.aeronave.AeronavePassageiros;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.SituacaoPlano;
import com.bcopstein.voos.domain.model.plano.Trecho;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Malha aérea inicial e aeronaves usadas nos testes de domínio (sem banco, sem Spring). */
public final class Fixtures {
    public static final Aeroporto POA = new Aeroporto("POA", "Porto Alegre", 41);
    public static final Aeroporto FLN = new Aeroporto("FLN", "Florianópolis", 16);
    public static final Aeroporto CWB = new Aeroporto("CWB", "Curitiba", 26);
    public static final Aeroporto GRU = new Aeroporto("GRU", "Guarulhos", 115);

    public static final Aerovia R1 = new Aerovia("R1", POA, FLN, new BigDecimal("363"));
    public static final Aerovia R2 = new Aerovia("R2", POA, GRU, new BigDecimal("865"));
    public static final Aerovia R3 = new Aerovia("R3", GRU, POA, new BigDecimal("865"));
    public static final Aerovia R4 = new Aerovia("R4", FLN, CWB, new BigDecimal("245"));
    public static final Aerovia R6 = new Aerovia("R6", CWB, GRU, new BigDecimal("360"));

    public static final Aeronave PASSAGEIROS = new AeronavePassageiros("PT-AZU", new BigDecimal("850"), new BigDecimal("4200"), "Azul", 174);
    public static final Aeronave PARTICULAR = new AeronaveParticular("PR-EXE", new BigDecimal("600"), new BigDecimal("2200"), "Helisul");
    public static final Aeronave POUCA_AUTONOMIA = new AeronaveParticular("PT-ECO", new BigDecimal("280"), new BigDecimal("500"), "Aeroclube");
    public static final Aeronave CARGA = new AeronaveCarga("PR-CRG", new BigDecimal("750"), new BigDecimal("5000"), "Total", new BigDecimal("35"));

    public static final Piloto PILOTO = new Piloto(101L, "Cmte. Carlos Silva");

    public static final LocalDateTime REF_08H = LocalDateTime.of(2027, 1, 15, 8, 0);

    private Fixtures() {}

    public static PlanoDeVoo plano(Aeronave aeronave, Aeroporto origem, Aeroporto destino, LocalDateTime partida, List<Trecho> trechos) {
        return PlanoDeVoo.reconstituir(1L, PILOTO, aeronave, origem, destino, partida, SituacaoPlano.EM_EDICAO, trechos);
    }
}
