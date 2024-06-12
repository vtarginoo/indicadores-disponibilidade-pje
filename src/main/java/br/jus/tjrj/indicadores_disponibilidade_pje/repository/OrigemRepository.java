package br.jus.tjrj.indicadores_disponibilidade_pje.repository;



import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrigemRepository extends JpaRepository<Origem, Long> {

    Optional<Origem> findByOrigem(Origem.OrigemEnum origem);



}
