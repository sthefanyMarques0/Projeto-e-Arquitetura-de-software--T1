package com.bcopstein.voos.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Trecho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Aerovia aerovia;

    private int altitude;
    private LocalDateTime entradaPrevista;
    private LocalDateTime chegadaPrevista;

    @Enumerated(EnumType.STRING)
    private TipoPassagem tipoPassagem;

    private int tempoPermanencia; // em minutos
    private int vagaPretendida;

    public Trecho() {}

    public Trecho(Aerovia aerovia, int altitude, LocalDateTime entradaPrevista, TipoPassagem tipoPassagem, int tempoPermanencia, int vagaPretendida) {
        this.aerovia = aerovia;
        this.altitude = altitude;
        this.entradaPrevista = entradaPrevista;
        this.tipoPassagem = tipoPassagem;
        this.tempoPermanencia = tempoPermanencia;
        this.vagaPretendida = vagaPretendida;
    }

    public Long getId() { return id; }
    public Aerovia getAerovia() { return aerovia; }
    public void setAerovia(Aerovia aerovia) { this.aerovia = aerovia; }
    public int getAltitude() { return altitude; }
    public void setAltitude(int altitude) { this.altitude = altitude; }
    public LocalDateTime getEntradaPrevista() { return entradaPrevista; }
    public void setEntradaPrevista(LocalDateTime entradaPrevista) { this.entradaPrevista = entradaPrevista; }
    public LocalDateTime getChegadaPrevista() { return chegadaPrevista; }
    public void setChegadaPrevista(LocalDateTime chegadaPrevista) { this.chegadaPrevista = chegadaPrevista; }
    public TipoPassagem getTipoPassagem() { return tipoPassagem; }
    public void setTipoPassagem(TipoPassagem tipoPassagem) { this.tipoPassagem = tipoPassagem; }
    public int getTempoPermanencia() { return tempoPermanencia; }
    public void setTempoPermanencia(int tempoPermanencia) { this.tempoPermanencia = tempoPermanencia; }
    public int getVagaPretendida() { return vagaPretendida; }
    public void setVagaPretendida(int vagaPretendida) { this.vagaPretendida = vagaPretendida; }
}
