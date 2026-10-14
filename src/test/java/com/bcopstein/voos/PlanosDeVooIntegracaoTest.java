package com.bcopstein.voos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bcopstein.voos.application.exception.PlanoInvalidoException;
import com.bcopstein.voos.application.port.in.AprovarPlano;
import com.bcopstein.voos.application.port.in.CancelarPlano;
import com.bcopstein.voos.application.port.in.ConsultarAeronaves;
import com.bcopstein.voos.application.port.in.ConsultarAeroportos;
import com.bcopstein.voos.application.port.in.ConsultarAerovias;
import com.bcopstein.voos.application.port.in.ConsultarPlanos;
import com.bcopstein.voos.application.port.in.CriarPlano;
import com.bcopstein.voos.application.port.in.DadosPlanoCommand;
import com.bcopstein.voos.application.port.in.DefinirPercurso;
import com.bcopstein.voos.application.port.in.TrechoCommand;
import com.bcopstein.voos.application.port.in.ValidarPlano;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.plano.PlanoDeVoo;
import com.bcopstein.voos.domain.model.plano.SituacaoPlano;
import com.bcopstein.voos.domain.model.plano.TipoPassagem;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sobe a aplicação com H2 em memória e as migrações do Flyway (mesma carga inicial da entrega).
 * Cada teste roda numa transação desfeita ao final, mantendo os dados iniciais intactos.
 * O relógio está fixo em 2026-11-19T12:00 (ver src/test/resources/application.properties).
 */
@SpringBootTest
@Transactional
class PlanosDeVooIntegracaoTest {
    @Autowired ConsultarAeroportos consultarAeroportos;
    @Autowired ConsultarAerovias consultarAerovias;
    @Autowired ConsultarAeronaves consultarAeronaves;
    @Autowired ConsultarPlanos consultarPlanos;
    @Autowired CriarPlano criarPlano;
    @Autowired DefinirPercurso definirPercurso;
    @Autowired ValidarPlano validarPlano;
    @Autowired AprovarPlano aprovarPlano;
    @Autowired CancelarPlano cancelarPlano;

    @Test
    void cargaInicialDisponivel() {
        assertEquals(4, consultarAeroportos.consultar().size());
        assertEquals(8, consultarAerovias.consultar().size());
        assertEquals(5, consultarAeronaves.consultar().size());
        assertEquals(5, consultarPlanos.porSituacao(SituacaoPlano.APROVADO).size());
        assertEquals(4, consultarPlanos.reservasDoPlano(1L).size());
    }

    @Test
    void validacaoNaoReserva() {
        assertTrue(validarPlano.validar(7L).isValido());
        assertEquals(0, consultarPlanos.reservasDoPlano(7L).size());
        assertEquals(SituacaoPlano.EM_EDICAO, consultarPlanos.porId(7L).getSituacao());
    }

    @Test
    void aprovacaoReservaECancelamentoLibera() {
        PlanoDeVoo aprovado = aprovarPlano.aprovar(7L);
        assertEquals(SituacaoPlano.APROVADO, aprovado.getSituacao());
        // R7 16:00–16:25:25 (slot 16) + vaga 10 de CWB 16:25:25–16:55:25 (slot 16)
        assertEquals(2, consultarPlanos.reservasDoPlano(7L).size());

        PlanoDeVoo cancelado = cancelarPlano.cancelar(7L);
        assertEquals(SituacaoPlano.CANCELADO, cancelado.getSituacao());
        assertEquals(0, consultarPlanos.reservasDoPlano(7L).size());
    }

    @Test
    void aprovacaoComConflitoNaoDeixaReservas() {
        PlanoInvalidoException erro = assertThrows(PlanoInvalidoException.class, () -> aprovarPlano.aprovar(6L));
        assertTrue(erro.getErros().stream().anyMatch(e -> e.contains("R1")), erro.getErros().toString());
        assertEquals(SituacaoPlano.EM_EDICAO, consultarPlanos.porId(6L).getSituacao());
        assertEquals(0, consultarPlanos.reservasDoPlano(6L).size());
    }

    @Test
    void vooJaExecutadoNaoPodeSerCancelado() {
        assertThrows(OperacaoNaoPermitidaException.class, () -> cancelarPlano.cancelar(5L));
        assertEquals(4, consultarPlanos.reservasDoPlano(5L).size());
    }

    @Test
    void criarDefinirPercursoEAprovar() {
        LocalDateTime partida = LocalDateTime.of(2026, 11, 20, 18, 0);
        PlanoDeVoo plano = criarPlano.criar(new DadosPlanoCommand(103L, "PP-GLO", "POA", "GRU", partida));
        assertEquals(SituacaoPlano.EM_EDICAO, plano.getSituacao());

        PlanoDeVoo comPercurso = definirPercurso.definir(plano.getId(),
                List.of(new TrechoCommand("R2", 33_000, partida, TipoPassagem.POUSO, 45, 20)));
        // 865 km / 850 km/h = 3663,53 s -> 3664 s = 1h01min04s
        assertEquals(LocalDateTime.of(2026, 11, 20, 19, 1, 4), comPercurso.getChegadaFinal().orElseThrow());

        assertEquals(SituacaoPlano.APROVADO, aprovarPlano.aprovar(plano.getId()).getSituacao());
        assertThrows(OperacaoNaoPermitidaException.class, () -> definirPercurso.definir(plano.getId(), List.of()));
    }
}
