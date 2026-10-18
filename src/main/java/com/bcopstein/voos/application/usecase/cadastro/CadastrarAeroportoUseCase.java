package com.bcopstein.voos.application.usecase.cadastro;

import com.bcopstein.voos.application.port.in.CadastrarAeroporto;
import com.bcopstein.voos.application.port.out.ConsultaAeroportosPort;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarAeroportoUseCase implements CadastrarAeroporto {
    private final ConsultaAeroportosPort aeroportos;

    public CadastrarAeroportoUseCase(ConsultaAeroportosPort aeroportos) {
        this.aeroportos = aeroportos;
    }

    @Override
    @Transactional
    public Aeroporto cadastrar(String codigo, String nome, int vagas) {
        Aeroporto aeroporto = new Aeroporto(codigo, nome, vagas);
        if (aeroportos.buscar(aeroporto.getCodigo()).isPresent()) {
            throw new OperacaoNaoPermitidaException("Já existe aeroporto com código " + codigo + ".");
        }
        return aeroportos.salvar(aeroporto);
    }
}
