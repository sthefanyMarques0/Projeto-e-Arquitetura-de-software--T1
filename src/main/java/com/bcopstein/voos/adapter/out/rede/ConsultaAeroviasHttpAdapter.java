package com.bcopstein.voos.adapter.out.rede;

import com.bcopstein.voos.application.port.out.ConsultaAeroviasPort;
import com.bcopstein.voos.application.port.out.ConsultaAeroportosPort;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
import com.bcopstein.voos.domain.model.rede.Aerovia;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Profile("!test")
public class ConsultaAeroviasHttpAdapter implements ConsultaAeroviasPort {
    private final RestClient client;
    private final ConsultaAeroportosPort aeroportos;

    public ConsultaAeroviasHttpAdapter(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder,
                                       ConsultaAeroportosPort aeroportos) {
        this.client = builder.baseUrl("http://cadastro-rede").build();
        this.aeroportos = aeroportos;
    }

    @Override
    public List<Aerovia> listar() {
        try {
            AeroviaDto[] response = client.get().uri("/api/aerovias").retrieve().body(AeroviaDto[].class);
            return Arrays.stream(response == null ? new AeroviaDto[0] : response)
                    .map(dto -> dto.toDomain(aeroportos)).toList();
        } catch (RestClientException e) {
            throw unavailable("listar aerovias", e);
        }
    }

    @Override
    public Optional<Aerovia> buscar(String id) {
        try {
            AeroviaDto response = client.get().uri("/api/aerovias/{id}", id).retrieve()
                    .onStatus(status -> status.value() == 404, (request, result) -> {})
                    .body(AeroviaDto.class);
            return Optional.ofNullable(response).map(dto -> dto.toDomain(aeroportos));
        } catch (RestClientException e) {
            throw unavailable("consultar aerovia " + id, e);
        }
    }

    @Override
    public Aerovia salvar(Aerovia aerovia) {
        throw new UnsupportedOperationException("O cadastro da rede é propriedade do serviço cadastro-rede.");
    }

    private static CadastroIndisponivelException unavailable(String operation, RestClientException cause) {
        return new CadastroIndisponivelException("Falha ao " + operation + " no serviço de cadastro da rede.", cause);
    }

    private record AeroviaDto(String id, String origem, String destino, BigDecimal distanciaKm) {
        Aerovia toDomain(ConsultaAeroportosPort aeroportos) {
            Aeroporto origemAeroporto = aeroportos.buscar(origem).orElseThrow(
                    () -> new CadastroIndisponivelException("Aeroporto de origem ausente no serviço de cadastro: " + origem, null));
            Aeroporto destinoAeroporto = aeroportos.buscar(destino).orElseThrow(
                    () -> new CadastroIndisponivelException("Aeroporto de destino ausente no serviço de cadastro: " + destino, null));
            return new Aerovia(id, origemAeroporto, destinoAeroporto, distanciaKm);
        }
    }
}
