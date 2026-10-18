package com.bcopstein.voos.application.usecase.cadastro;

import com.bcopstein.voos.application.exception.RecursoNaoEncontradoException;
import com.bcopstein.voos.application.port.in.CadastrarAerovia;
import com.bcopstein.voos.application.port.out.ConsultaAeroportosPort;
import com.bcopstein.voos.application.port.out.ConsultaAeroviasPort;
import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarAeroviaUseCase implements CadastrarAerovia {
    private final ConsultaAeroviasPort aerovias;
    private final ConsultaAeroportosPort aeroportos;

    public CadastrarAeroviaUseCase(ConsultaAeroviasPort aerovias, ConsultaAeroportosPort aeroportos) {
        this.aerovias = aerovias;
        this.aeroportos = aeroportos;
    }

    @Override
    @Transactional
    public Aerovia cadastrar(String id, String origem, String destino, BigDecimal distanciaKm) {
        if (aerovias.buscar(id).isPresent()) {
            throw new OperacaoNaoPermitidaException("Já existe aerovia com identificador " + id + ". Aerovias não podem ser alteradas.");
        }
        if (origem.equals(destino)) {
            throw new DadosInvalidosException("Origem e destino da aerovia devem ser diferentes.");
        }
        Aerovia aerovia = new Aerovia(id, aeroporto(origem), aeroporto(destino), distanciaKm);
        return aerovias.salvar(aerovia);
    }

    private Aeroporto aeroporto(String codigo) {
        return aeroportos.buscar(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto não encontrado: " + codigo));
    }
}
