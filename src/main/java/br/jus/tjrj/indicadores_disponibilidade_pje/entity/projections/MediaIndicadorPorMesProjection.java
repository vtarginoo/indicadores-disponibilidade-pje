package br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections;

public interface MediaIndicadorPorMesProjection {
    int getMes();
    String getOrigem();
    Long getSomaQuantidade();
}