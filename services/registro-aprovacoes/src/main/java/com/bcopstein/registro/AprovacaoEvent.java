package com.bcopstein.registro;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AprovacaoEvent(
        String eventoId,
        String versaoContrato,
        Long planoId,
        LocalDateTime aprovadoEm,
        String criadoPor,
        Piloto piloto,
        Aeronave aeronave,
        String origem,
        String destino,
        LocalDateTime partidaPrevista,
        List<Trecho> trechos) {
    public record Piloto(Long id, String nome) {}
    public record Aeronave(String prefixo, String categoria, BigDecimal velocidadeCruzeiroKmH,
                           BigDecimal autonomiaNominalKm, String empresaManutencao, String companhiaAerea,
                           Integer capacidadeMaximaPassageiros, BigDecimal capacidadeMaximaToneladas) {}
    public record Trecho(int ordem, String aeroviaId, String origem, String destino, BigDecimal distanciaKm,
                         int altitude, LocalDateTime entradaPrevista, LocalDateTime chegadaPrevista,
                         String tipoPassagem, Integer permanenciaMinutos, Integer vagaPretendida) {}
}
