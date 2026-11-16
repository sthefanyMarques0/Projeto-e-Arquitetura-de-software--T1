package com.bcopstein.voos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bcopstein.voos.application.port.in.AbandonarPlano;
import com.bcopstein.voos.application.port.in.AprovarPlano;
import com.bcopstein.voos.application.port.in.ConsultarDisponibilidade;
import com.bcopstein.voos.application.port.in.ConsultarPlanos;
import com.bcopstein.voos.application.port.in.CriarPlano;
import com.bcopstein.voos.application.port.in.DadosPlanoCommand;
import com.bcopstein.voos.application.port.in.DefinirPercurso;
import com.bcopstein.voos.application.port.in.EditarPlano;
import com.bcopstein.voos.application.port.in.TrechoCommand;
import com.bcopstein.voos.application.port.in.ValidarPlano;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.plano.ResultadoValidacao;
import com.bcopstein.voos.domain.model.plano.SituacaoPlano;
import com.bcopstein.voos.domain.model.plano.TipoPassagem;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reproduz, pelos casos de uso, o roteiro da apresentação em docs/api/planos-de-voo.postman_collection.json
 * (pastas 2 e 5), garantindo que cada requisição da demonstração produz o resultado esperado na data de referência.
 */
@SpringBootTest
@Transactional
class RoteiroDemonstracaoTest {
    private static final LocalDateTime ONZE = LocalDateTime.of(2027, 1, 15, 11, 0);
    private static final LocalDateTime OITO = LocalDateTime.of(2027, 1, 15, 8, 0);

    @Autowired CriarPlano criarPlano;
    @Autowired EditarPlano editarPlano;
    @Autowired DefinirPercurso definirPercurso;
    @Autowired ValidarPlano validarPlano;
    @Autowired AprovarPlano aprovarPlano;
    @Autowired AbandonarPlano abandonarPlano;
    @Autowired ConsultarPlanos consultarPlanos;
    @Autowired ConsultarDisponibilidade consultarDisponibilidade;

    private static boolean contem(ResultadoValidacao r, String texto) {
        return r.getErros().stream().anyMatch(e -> e.contains(texto));
    }

    @Test
    void disponibilidadeNaDataDeReferencia() {
        List<Integer> r1 = consultarDisponibilidade.altitudesDisponiveis("R1", OITO, OITO.plusHours(1), null);
        assertFalse(r1.contains(29_000), "29.000 pés em R1 está reservada pelo plano 1");
        assertEquals(10, r1.size());
        assertEquals(List.of(25_000, 26_000, 27_000),
                consultarDisponibilidade.altitudesDisponiveis("R1", OITO, OITO.plusHours(1), "PR-EXE"));
        List<Integer> cwb = consultarDisponibilidade.vagasDisponiveis("CWB", OITO, OITO.plusHours(2));
        assertFalse(cwb.contains(1), "vaga 1 de CWB está reservada pelo plano 1");
        assertEquals(25, cwb.size());
    }

    @Test
    void cicloCompletoComTodasAsFalhasDeValidacao() {
        Long id = criarPlano.criar(new DadosPlanoCommand(103L, "PT-AZU", "POA", "CWB", ONZE)).getId();

        definirPercurso.definir(id, List.of(
                new TrechoCommand("R1", 30_000, ONZE, TipoPassagem.SOBREVOO, null, null),
                new TrechoCommand("R6", 30_000, LocalDateTime.of(2027, 1, 15, 11, 25, 38), TipoPassagem.POUSO, 30, 5)));
        assertTrue(contem(validarPlano.validar(id), "descontínuo"), "percurso inconsistente");

        definirPercurso.definir(id, List.of(
                new TrechoCommand("R1", 30_000, ONZE, TipoPassagem.SOBREVOO, null, null),
                new TrechoCommand("R4", 30_000, LocalDateTime.of(2027, 1, 15, 11, 20), TipoPassagem.POUSO, 30, 5)));
        assertTrue(contem(validarPlano.validar(id), "incompatível com a duração calculada"), "horário incompatível");

        definirPercurso.definir(id, List.of(
                new TrechoCommand("R1", 27_000, ONZE, TipoPassagem.SOBREVOO, null, null),
                new TrechoCommand("R4", 27_000, LocalDateTime.of(2027, 1, 15, 11, 25, 38), TipoPassagem.POUSO, 30, 5)));
        assertTrue(contem(validarPlano.validar(id), "passageiros"), "restrição da aeronave");

        editarPlano.editar(id, new DadosPlanoCommand(103L, "PT-ECO", "POA", "CWB", ONZE));
        definirPercurso.definir(id, List.of(
                new TrechoCommand("R1", 25_000, ONZE, TipoPassagem.SOBREVOO, null, null),
                new TrechoCommand("R4", 25_000, LocalDateTime.of(2027, 1, 15, 12, 17, 48), TipoPassagem.POUSO, 30, 5)));
        ResultadoValidacao autonomia = validarPlano.validar(id);
        assertTrue(contem(autonomia, "autonomia"), autonomia.getErros().toString());
        assertEquals(1, autonomia.getErros().size(), "só a autonomia deve falhar: " + autonomia.getErros());

        editarPlano.editar(id, new DadosPlanoCommand(103L, "PT-AZU", "POA", "CWB", OITO));
        definirPercurso.definir(id, List.of(
                new TrechoCommand("R1", 30_000, OITO, TipoPassagem.SOBREVOO, null, null),
                new TrechoCommand("R4", 30_000, LocalDateTime.of(2027, 1, 15, 8, 25, 38), TipoPassagem.POUSO, 30, 1)));
        ResultadoValidacao vaga = validarPlano.validar(id);
        assertTrue(contem(vaga, "vaga 1 de CWB indisponível"), vaga.getErros().toString());
        assertEquals(1, vaga.getErros().size(), "só a vaga deve falhar: " + vaga.getErros());

        definirPercurso.definir(id, List.of(
                new TrechoCommand("R1", 30_000, OITO, TipoPassagem.SOBREVOO, null, null),
                new TrechoCommand("R4", 30_000, LocalDateTime.of(2027, 1, 15, 8, 25, 38), TipoPassagem.POUSO, 30, 7)));
        ResultadoValidacao valido = validarPlano.validar(id);
        assertTrue(valido.isValido(), valido.getErros().toString());

        abandonarPlano.abandonar(id);
        assertThrows(OperacaoNaoPermitidaException.class, () -> aprovarPlano.aprovar(id));
        assertTrue(consultarPlanos.porSituacao(SituacaoPlano.ABANDONADO).stream().anyMatch(p -> p.getId().equals(id)));
    }
}
