package com.bcopstein.voos.adapter.out.persistence.adapter;

import com.bcopstein.voos.adapter.out.persistence.mapper.RedeAereaPersistenceMapper;
import com.bcopstein.voos.adapter.out.persistence.repository.AeroviaJpaRepository;
import com.bcopstein.voos.application.port.out.ConsultaAeroportosPort;
import com.bcopstein.voos.application.port.out.ConsultaAeroviasPort;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ConsultaAeroviasJpaAdapter implements ConsultaAeroviasPort {
    private final AeroviaJpaRepository repository;
    private final ConsultaAeroportosPort aeroportos;

    public ConsultaAeroviasJpaAdapter(AeroviaJpaRepository repository, ConsultaAeroportosPort aeroportos) {
        this.repository = repository;
        this.aeroportos = aeroportos;
    }

    @Override
    public List<Aerovia> listar() {
        Map<String, Aeroporto> porCodigo = aeroportosPorCodigo();
        return repository.findAll(Sort.by("id")).stream()
                .map(a -> RedeAereaPersistenceMapper.toDomain(a, porCodigo)).toList();
    }

    @Override
    public Optional<Aerovia> buscar(String id) {
        return repository.findById(id).map(a -> RedeAereaPersistenceMapper.toDomain(a, aeroportosPorCodigo()));
    }

    private Map<String, Aeroporto> aeroportosPorCodigo() {
        return aeroportos.listar().stream().collect(Collectors.toMap(Aeroporto::getCodigo, Function.identity()));
    }
}
