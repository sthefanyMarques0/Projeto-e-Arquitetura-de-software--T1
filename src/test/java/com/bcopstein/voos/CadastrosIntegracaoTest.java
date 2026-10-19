package com.bcopstein.voos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bcopstein.voos.application.exception.RecursoNaoEncontradoException;
import com.bcopstein.voos.application.port.in.CadastrarAeronave;
import com.bcopstein.voos.application.port.in.CadastrarAeroporto;
import com.bcopstein.voos.application.port.in.CadastrarAerovia;
import com.bcopstein.voos.application.port.in.CadastrarPiloto;
import com.bcopstein.voos.application.port.in.ConsultarAeronaves;
import com.bcopstein.voos.application.port.in.ConsultarAeroportos;
import com.bcopstein.voos.application.port.in.ConsultarAerovias;
import com.bcopstein.voos.application.port.in.ConsultarPilotos;
import com.bcopstein.voos.application.port.in.DadosAeronaveCommand;
import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.aeronave.AeronaveCarga;
import com.bcopstein.voos.domain.model.aeronave.CategoriaAeronave;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CadastrosIntegracaoTest {
    @Autowired CadastrarAeroporto cadastrarAeroporto;
    @Autowired CadastrarAerovia cadastrarAerovia;
    @Autowired CadastrarPiloto cadastrarPiloto;
    @Autowired CadastrarAeronave cadastrarAeronave;
    @Autowired ConsultarAeroportos consultarAeroportos;
    @Autowired ConsultarAerovias consultarAerovias;
    @Autowired ConsultarPilotos consultarPilotos;
    @Autowired ConsultarAeronaves consultarAeronaves;

    @Test
    void cadastraAeroportoEAerovia() {
        cadastrarAeroporto.cadastrar("SSA", "Aeroporto de Salvador", 30);
        cadastrarAerovia.cadastrar("R9", "POA", "SSA", new BigDecimal("2340"));

        assertEquals(5, consultarAeroportos.consultar().size());
        assertTrue(consultarAerovias.consultar().stream().anyMatch(a -> a.getId().equals("R9")));
    }

    @Test
    void aeroviaExistenteNaoPodeSerSobrescrita() {
        assertThrows(OperacaoNaoPermitidaException.class,
                () -> cadastrarAerovia.cadastrar("R1", "POA", "GRU", new BigDecimal("865")));
        assertEquals(0, new BigDecimal("363").compareTo(consultarAerovias.consultar().stream()
                .filter(a -> a.getId().equals("R1")).findFirst().orElseThrow().getDistanciaKm()));
    }

    @Test
    void aeroviaExigeAeroportosExistentesEDistintos() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> cadastrarAerovia.cadastrar("R9", "POA", "XXX", new BigDecimal("100")));
        assertThrows(DadosInvalidosException.class,
                () -> cadastrarAerovia.cadastrar("R9", "POA", "POA", new BigDecimal("100")));
    }

    @Test
    void aeroportoDuplicadoRecusado() {
        assertThrows(OperacaoNaoPermitidaException.class, () -> cadastrarAeroporto.cadastrar("POA", "Outro", 10));
    }

    @Test
    void cadastraPiloto() {
        cadastrarPiloto.cadastrar(105L, "Cmte. Joana Lima");
        assertEquals(5, consultarPilotos.consultar().size());
        assertThrows(OperacaoNaoPermitidaException.class, () -> cadastrarPiloto.cadastrar(101L, "Repetido"));
    }

    @Test
    void cadastraAeronaveDeCadaCategoria() {
        cadastrarAeronave.cadastrar(new DadosAeronaveCommand("PT-NEW", CategoriaAeronave.PARTICULAR,
                new BigDecimal("500"), new BigDecimal("1500"), "Oficina X", null, null, null));
        cadastrarAeronave.cadastrar(new DadosAeronaveCommand("PR-PAX", CategoriaAeronave.COMERCIAL_PASSAGEIROS,
                new BigDecimal("840"), new BigDecimal("4000"), null, "LATAM", 180, null));
        cadastrarAeronave.cadastrar(new DadosAeronaveCommand("PR-CAR", CategoriaAeronave.COMERCIAL_CARGA,
                new BigDecimal("700"), new BigDecimal("4500"), null, "Sideral", null, new BigDecimal("20")));

        assertEquals(8, consultarAeronaves.consultar().size());
        assertTrue(consultarAeronaves.consultar().stream()
                .anyMatch(a -> a.getPrefixo().equals("PR-CAR") && a instanceof AeronaveCarga));
    }

    @Test
    void aeronaveComCamposIncoerentesRecusada() {
        assertThrows(DadosInvalidosException.class, () -> cadastrarAeronave.cadastrar(new DadosAeronaveCommand(
                "PT-ERR", CategoriaAeronave.PARTICULAR, new BigDecimal("500"), new BigDecimal("1500"),
                null, null, null, null)));
        assertThrows(DadosInvalidosException.class, () -> cadastrarAeronave.cadastrar(new DadosAeronaveCommand(
                "PT-ERR", CategoriaAeronave.COMERCIAL_CARGA, new BigDecimal("500"), new BigDecimal("1500"),
                null, "Cia", 100, new BigDecimal("10"))));
        assertThrows(OperacaoNaoPermitidaException.class, () -> cadastrarAeronave.cadastrar(new DadosAeronaveCommand(
                "PT-AZU", CategoriaAeronave.COMERCIAL_PASSAGEIROS, new BigDecimal("850"), new BigDecimal("4200"),
                null, "Azul", 174, null)));
    }
}
