package com.bcopstein.cadastro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;

@SpringBootTest
class CadastroControllerTest {
    @Autowired CadastroController controller;

    @Test
    void migrationLoadsInitialNetworkAndFilterWorks() {
        assertEquals(4, controller.aeroportos().size());
        assertEquals(8, controller.aerovias(null, null).size());
        assertEquals(2, controller.aerovias("POA", null).size());
        assertEquals(41, controller.vagas("POA").numeros().size());
    }

    @Test
    void repeatedCodesDoNotOverwriteInitialData() {
        assertThrows(DuplicateKeyException.class,
                () -> controller.cadastrarAeroporto(new CadastroController.NovoAeroporto("POA", "Alterado", 1)));
        assertEquals("Aeroporto Internacional Salgado Filho - Porto Alegre",
                controller.aeroporto("POA").nome());
        assertThrows(DuplicateKeyException.class,
                () -> controller.cadastrarAerovia(new CadastroController.NovaAerovia(
                        "R1", "POA", "GRU", new BigDecimal("900"))));
        assertEquals(0, new BigDecimal("363").compareTo(controller.aerovia("R1").distanciaKm()));
    }
}
