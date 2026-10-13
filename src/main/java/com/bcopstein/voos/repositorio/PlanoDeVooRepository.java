package com.bcopstein.voos.repositorio;

import com.bcopstein.voos.dominio.PlanoDeVoo;
import com.bcopstein.voos.dominio.SituacaoPlano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanoDeVooRepository extends JpaRepository<PlanoDeVoo, Long> {
    List<PlanoDeVoo> findBySituacao(SituacaoPlano situacao);
}
