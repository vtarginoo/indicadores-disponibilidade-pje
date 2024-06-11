package br.jus.tjrj.indicadores_disponibilidade_pje.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record DadosMediaMes(

        @NotNull @Min(1900) @Max(2100)
        int ano,

        @NotNull @Min(1) @Max(12)
        int mes,

        @NotEmpty
        List<IndicadorMensal> indicadores
) {
    public record IndicadorMensal(
            @NotBlank
            String origem,

            @NotNull @PositiveOrZero
            double media
    ) {}
}