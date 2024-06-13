package br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections;

import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;

public interface IndicadorHoraMesProjection {
    Integer getAno();
    Integer getMes();
    Origem.OrigemEnum getOrigem(); // Usando o enum diretamente
    Integer getHora();
    Double getMedia();
}