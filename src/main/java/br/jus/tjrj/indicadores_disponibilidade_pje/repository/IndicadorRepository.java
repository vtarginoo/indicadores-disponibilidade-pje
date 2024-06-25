package br.jus.tjrj.indicadores_disponibilidade_pje.repository;

import br.jus.tjrj.indicadores_disponibilidade_pje.entity.IndicadorDisponibilidade;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.IndicadorHoraMesProjection;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.IndicadoresDiaProjection;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.MediaIndicadorPorMesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface IndicadorRepository extends JpaRepository<IndicadorDisponibilidade, Long> {

    @Query("SELECT MONTH(i.data) AS mes, o.origem AS origem, SUM(i.quantidade) AS somaQuantidade" +
            " FROM IndicadorDisponibilidade i JOIN i.origem o " +
            "WHERE YEAR(i.data) = :ano GROUP BY MONTH(i.data), o.origem")
    List<MediaIndicadorPorMesProjection> findMediaIndicadorPorMes(int ano);



    @Query("SELECT i.data as data, i.diaDaSemana as diaDaSemana, DAY(i.data) as dia, " +
            "i.origem.origem as origem, SUM(i.quantidade) as quantidade " +
            "FROM IndicadorDisponibilidade i " +
            "WHERE YEAR(i.data) = :ano " +
            "GROUP BY i.data, i.diaDaSemana, DAY(i.data), i.origem.origem") // Agrupando pela origem corretamente
    List<IndicadoresDiaProjection> findIndicadoresPorDiaAgrupados(@Param("ano") int ano);


    List<IndicadorDisponibilidade> findByOrigemOrigemAndData(
            Origem.OrigemEnum origem,
            LocalDate data
    );


    @Query("SELECT YEAR(i.data) as ano, MONTH(i.data) as mes, i.origem.origem as origem, i.hora as hora, " +
            "AVG(i.quantidade) as media " +
            "FROM IndicadorDisponibilidade i " +
            "WHERE i.origem.origem = :origem AND YEAR(i.data) = :ano AND MONTH(i.data) = :mes " +
            "GROUP BY YEAR(i.data), MONTH(i.data), i.origem.origem, i.hora")
    List<IndicadorHoraMesProjection> findByOrigemEMesEAno(
            @Param("origem") Origem.OrigemEnum origem,
            @Param("mes") int mes,
            @Param("ano") int ano
    );
}




