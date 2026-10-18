package com.bcopstein.voos.adapter.out.persistence.adapter;

import com.bcopstein.voos.adapter.out.persistence.mapper.RedeAereaPersistenceMapper;
import com.bcopstein.voos.adapter.out.persistence.repository.AeroportoJpaRepository;
import com.bcopstein.voos.application.port.out.ConsultaAeroportosPort;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ConsultaAeroportosJpaAdapter implements ConsultaAeroportosPort {
    private final AeroportoJpaRepository repository;

    public ConsultaAeroportosJpaAdapter(AeroportoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Aeroporto> listar() {
        return repository.findAll(Sort.by("codigo")).stream().map(RedeAereaPersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Aeroporto> buscar(String codigo) {
        return repository.findById(codigo).map(RedeAereaPersistenceMapper::toDomain);
    }

    @Override
    public Aeroporto salvar(Aeroporto aeroporto) {
        return RedeAereaPersistenceMapper.toDomain(repository.save(RedeAereaPersistenceMapper.toEntity(aeroporto)));
    }
}
