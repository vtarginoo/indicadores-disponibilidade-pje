package br.jus.tjrj.indicadores_disponibilidade_pje.repository;

import br.jus.tjrj.indicadores_disponibilidade_pje.entity.IndicadorDisponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface IndicadorRepository extends JpaRepository<IndicadorDisponibilidade, Long> {

    @Query("SELECT MONTH(i.data), o.origem, SUM(i.quantidade)" +
            " FROM IndicadorDisponibilidade i JOIN i.origem o " +
            "WHERE YEAR(i.data) = :ano GROUP BY MONTH(i.data), o.origem")
    List<Object[]> findMediaIndicadorPorMes(int ano);

    @Query("SELECT i.data, i.diaDaSemana, DAY(i.data) as dia, i.origem.origem as origem, " +
            "SUM(i.quantidade) as quantidade " +
            "FROM IndicadorDisponibilidade i " +
            "WHERE YEAR(i.data) = :ano " +
            "GROUP BY i.data, i.diaDaSemana, DAY(i.data), i.origem.origem")
    List<Object[]> findIndicadoresPorDiaAgrupados(@Param("ano") int ano);

    @Query("SELECT i FROM IndicadorDisponibilidade i" +
            " JOIN i.origem o " +
            "WHERE o.origem = :origem AND i.data = :data")
    List<IndicadorDisponibilidade> findByOrigemAndData(@Param("origem") String origem, @Param("data") LocalDate data);

    @Query("SELECT YEAR(i.data), MONTH(i.data), o.origem, i.hora, AVG(i.quantidade)" +
            "FROM IndicadorDisponibilidade i " +
            "JOIN i.origem o " +
            "WHERE o.origem = :origem " +
            "  AND YEAR(i.data) = :ano " +
            "  AND MONTH(i.data) = :mes " +
            "GROUP BY YEAR(i.data), MONTH(i.data), o.origem, i.hora")
    List<Object[]> findByOrigemEMesEAno(
            @Param("origem") String origem,
            @Param("mes") int mes,
            @Param("ano") int ano
    );

}




