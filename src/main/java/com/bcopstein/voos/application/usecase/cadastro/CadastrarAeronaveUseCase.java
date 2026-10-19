package com.bcopstein.voos.application.usecase.cadastro;

import com.bcopstein.voos.application.port.in.CadastrarAeronave;
import com.bcopstein.voos.application.port.in.DadosAeronaveCommand;
import com.bcopstein.voos.application.port.out.ConsultaAeronavesPort;
import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import com.bcopstein.voos.domain.model.aeronave.AeronaveCarga;
import com.bcopstein.voos.domain.model.aeronave.AeronaveParticular;
import com.bcopstein.voos.domain.model.aeronave.AeronavePassageiros;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarAeronaveUseCase implements CadastrarAeronave {
    private final ConsultaAeronavesPort aeronaves;

    public CadastrarAeronaveUseCase(ConsultaAeronavesPort aeronaves) {
        this.aeronaves = aeronaves;
    }

    @Override
    @Transactional
    public Aeronave cadastrar(DadosAeronaveCommand dados) {
        if (dados.categoria() == null) {
            throw new DadosInvalidosException("Aeronave deve ter categoria.");
        }
        Aeronave aeronave = criar(dados);
        if (aeronaves.buscar(aeronave.getPrefixo()).isPresent()) {
            throw new OperacaoNaoPermitidaException("Já existe aeronave com prefixo " + aeronave.getPrefixo() + ".");
        }
        return aeronaves.salvar(aeronave);
    }

    private static Aeronave criar(DadosAeronaveCommand d) {
        return switch (d.categoria()) {
            case PARTICULAR -> {
                exigirTexto(d.empresaManutencao(), "Aeronave particular deve informar a empresa de manutenção.");
                exigirAusente(d.companhiaAerea() != null || d.capacidadeMaximaPassageiros() != null
                        || d.capacidadeMaximaToneladas() != null, "Aeronave particular não possui companhia aérea nem capacidade.");
                yield new AeronaveParticular(d.prefixo(), d.velocidadeCruzeiroKmH(), d.autonomiaNominalKm(),
                        d.empresaManutencao());
            }
            case COMERCIAL_PASSAGEIROS -> {
                exigirTexto(d.companhiaAerea(), "Aeronave comercial deve informar a companhia aérea.");
                if (d.capacidadeMaximaPassageiros() == null || d.capacidadeMaximaPassageiros() <= 0) {
                    throw new DadosInvalidosException("Aeronave de passageiros deve ter capacidade de passageiros positiva.");
                }
                exigirAusente(d.empresaManutencao() != null || d.capacidadeMaximaToneladas() != null,
                        "Aeronave de passageiros não possui empresa de manutenção nem capacidade em toneladas.");
                yield new AeronavePassageiros(d.prefixo(), d.velocidadeCruzeiroKmH(), d.autonomiaNominalKm(),
                        d.companhiaAerea(), d.capacidadeMaximaPassageiros());
            }
            case COMERCIAL_CARGA -> {
                exigirTexto(d.companhiaAerea(), "Aeronave comercial deve informar a companhia aérea.");
                if (d.capacidadeMaximaToneladas() == null || d.capacidadeMaximaToneladas().signum() <= 0) {
                    throw new DadosInvalidosException("Aeronave de carga deve ter capacidade em toneladas positiva.");
                }
                exigirAusente(d.empresaManutencao() != null || d.capacidadeMaximaPassageiros() != null,
                        "Aeronave de carga não possui empresa de manutenção nem capacidade de passageiros.");
                yield new AeronaveCarga(d.prefixo(), d.velocidadeCruzeiroKmH(), d.autonomiaNominalKm(),
                        d.companhiaAerea(), d.capacidadeMaximaToneladas());
            }
        };
    }

    private static void exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(mensagem);
        }
    }

    private static void exigirAusente(boolean presente, String mensagem) {
        if (presente) {
            throw new DadosInvalidosException(mensagem);
        }
    }
}
