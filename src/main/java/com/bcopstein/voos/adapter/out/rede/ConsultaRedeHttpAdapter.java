package com.bcopstein.voos.adapter.out.rede;

import com.bcopstein.voos.application.port.out.ConsultaAeroportosPort;
import com.bcopstein.voos.domain.model.rede.Aeroporto;
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
public class ConsultaRedeHttpAdapter implements ConsultaAeroportosPort {
    private final RestClient client;

    public ConsultaRedeHttpAdapter(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.client = builder.baseUrl("http://cadastro-rede").build();
    }

    @Override
    public List<Aeroporto> listar() {
        try {
            AeroportoDto[] response = client.get().uri("/api/aeroportos").retrieve().body(AeroportoDto[].class);
            return Arrays.stream(response == null ? new AeroportoDto[0] : response)
                    .map(AeroportoDto::toDomain).toList();
        } catch (RestClientException e) {
            throw unavailable("listar aeroportos", e);
        }
    }

    @Override
    public Optional<Aeroporto> buscar(String codigo) {
        try {
            AeroportoDto response = client.get().uri("/api/aeroportos/{codigo}", codigo).retrieve()
                    .onStatus(status -> status.value() == 404, (request, result) -> {})
                    .body(AeroportoDto.class);
            return Optional.ofNullable(response).map(AeroportoDto::toDomain);
        } catch (RestClientException e) {
            throw unavailable("consultar aeroporto " + codigo, e);
        }
    }

    @Override
    public Aeroporto salvar(Aeroporto aeroporto) {
        throw new UnsupportedOperationException("O cadastro da rede é propriedade do serviço cadastro-rede.");
    }

    private static CadastroIndisponivelException unavailable(String operation, RestClientException cause) {
        return new CadastroIndisponivelException("Falha ao " + operation + " no serviço de cadastro da rede.", cause);
    }

    private record AeroportoDto(String codigo, String nome, int vagas) {
        Aeroporto toDomain() { return new Aeroporto(codigo, nome, vagas); }
    }

}
