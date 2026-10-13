package com.bcopstein.voos.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoReserva tipoReserva;

    private String identificadorRecurso; // Ex: R1_28000 ou POA_VAGA_1

    @ManyToOne
    private Aerovia aerovia;

    @ManyToOne
    private Aeroporto aeroporto;

    private int altitude;
    private int vaga;

    private LocalDateTime slotInicio;
    private LocalDateTime slotFim;

    @ManyToOne(optional = false)
    private PlanoDeVoo plano;

    public Reserva() {}

    public static Reserva criarReservaAerovia(Aerovia aerovia, int altitude, Slot slot, PlanoDeVoo plano) {
        Reserva r = new Reserva();
        r.tipoReserva = TipoReserva.ALTITUDE_AEROVIA;
        r.aerovia = aerovia;
        r.altitude = altitude;
        r.identificadorRecurso = aerovia.getId() + "_ALT_" + altitude;
        r.slotInicio = slot.getInicio();
        r.slotFim = slot.getFim();
        r.plano = plano;
        return r;
    }

    public static Reserva criarReservaAeroporto(Aeroporto aeroporto, int vaga, Slot slot, PlanoDeVoo plano) {
        Reserva r = new Reserva();
        r.tipoReserva = TipoReserva.VAGA_AEROPORTO;
        r.aeroporto = aeroporto;
        r.vaga = vaga;
        r.identificadorRecurso = aeroporto.getCodigo() + "_VAGA_" + vaga;
        r.slotInicio = slot.getInicio();
        r.slotFim = slot.getFim();
        r.plano = plano;
        return r;
    }

    public Long getId() { return id; }
    public TipoReserva getTipoReserva() { return tipoReserva; }
    public String getIdentificadorRecurso() { return identificadorRecurso; }
    public Aerovia getAerovia() { return aerovia; }
    public Aeroporto getAeroporto() { return aeroporto; }
    public int getAltitude() { return altitude; }
    public int getVaga() { return vaga; }
    public LocalDateTime getSlotInicio() { return slotInicio; }
    public LocalDateTime getSlotFim() { return slotFim; }
    public PlanoDeVoo getPlano() { return plano; }
}
