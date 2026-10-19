package com.bcopstein.voos.adapter.out.persistence.adapter;

import com.bcopstein.voos.adapter.out.persistence.mapper.RedeAereaPersistenceMapper;
import com.bcopstein.voos.adapter.out.persistence.repository.PilotoJpaRepository;
import com.bcopstein.voos.application.port.out.ConsultaPilotosPort;
import com.bcopstein.voos.domain.model.piloto.Piloto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ConsultaPilotosJpaAdapter implements ConsultaPilotosPort {
    private final PilotoJpaRepository repository;

    public ConsultaPilotosJpaAdapter(PilotoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Piloto> listar() {
        return repository.findAll(Sort.by("id")).stream().map(RedeAereaPersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Piloto> buscar(Long id) {
        return repository.findById(id).map(RedeAereaPersistenceMapper::toDomain);
    }

    @Override
    public Piloto salvar(Piloto piloto) {
        return RedeAereaPersistenceMapper.toDomain(repository.save(RedeAereaPersistenceMapper.toEntity(piloto)));
    }
}
