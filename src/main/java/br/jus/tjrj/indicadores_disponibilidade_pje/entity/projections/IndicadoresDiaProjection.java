package br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections;

import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;

import java.time.LocalDate;

public interface IndicadoresDiaProjection {
    LocalDate getData();
    Integer getDiaDaSemana();
    Integer getDia();
    Origem.OrigemEnum getOrigem(); // Usando o enum diretamente
    Integer getQuantidade();

}
