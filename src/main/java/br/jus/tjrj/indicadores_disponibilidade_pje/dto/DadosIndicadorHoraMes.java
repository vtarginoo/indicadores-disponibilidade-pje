package br.jus.tjrj.indicadores_disponibilidade_pje.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record DadosIndicadorHoraMes(
        @NotNull @Min(1900) @Max(2100)
        int ano,

        @NotNull @Min(1) @Max(12)
        int mes,

        @NotBlank
        String origem,

        @NotEmpty
        List<DadosIndicadorHoraMes.IndicadorHoraMes> indicador
) {
    public record IndicadorHoraMes(
            @NotNull @Min(0) @Max(23)
            int hora,

            @NotNull @PositiveOrZero
            double media
    ) {}

}
