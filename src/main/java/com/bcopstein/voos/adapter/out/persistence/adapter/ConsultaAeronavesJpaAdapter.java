package com.bcopstein.voos.adapter.out.persistence.adapter;

import com.bcopstein.voos.adapter.out.persistence.mapper.AeronavePersistenceMapper;
import com.bcopstein.voos.adapter.out.persistence.repository.AeronaveJpaRepository;
import com.bcopstein.voos.application.port.out.ConsultaAeronavesPort;
import com.bcopstein.voos.domain.model.aeronave.Aeronave;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ConsultaAeronavesJpaAdapter implements ConsultaAeronavesPort {
    private final AeronaveJpaRepository repository;

    public ConsultaAeronavesJpaAdapter(AeronaveJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Aeronave> listar() {
        return repository.findAll(Sort.by("prefixo")).stream().map(AeronavePersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Aeronave> buscar(String prefixo) {
        return repository.findById(prefixo).map(AeronavePersistenceMapper::toDomain);
    }
}
