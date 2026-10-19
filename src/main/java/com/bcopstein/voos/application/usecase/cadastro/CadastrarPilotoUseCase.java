package com.bcopstein.voos.application.usecase.cadastro;

import com.bcopstein.voos.application.port.in.CadastrarPiloto;
import com.bcopstein.voos.application.port.out.ConsultaPilotosPort;
import com.bcopstein.voos.domain.exception.DadosInvalidosException;
import com.bcopstein.voos.domain.exception.OperacaoNaoPermitidaException;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarPilotoUseCase implements CadastrarPiloto {
    private final ConsultaPilotosPort pilotos;

    public CadastrarPilotoUseCase(ConsultaPilotosPort pilotos) {
        this.pilotos = pilotos;
    }

    @Override
    @Transactional
    public Piloto cadastrar(Long id, String nome) {
        if (id == null || nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("Piloto deve ter identificador e nome.");
        }
        if (pilotos.buscar(id).isPresent()) {
            throw new OperacaoNaoPermitidaException("Já existe piloto com identificador " + id + ".");
        }
        return pilotos.salvar(new Piloto(id, nome));
    }
}
