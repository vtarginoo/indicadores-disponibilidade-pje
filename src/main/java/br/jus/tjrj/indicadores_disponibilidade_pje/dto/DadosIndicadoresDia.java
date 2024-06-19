package br.jus.tjrj.indicadores_disponibilidade_pje.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record DadosIndicadoresDia(

        @NotNull @Min(1900) @Max(2100)
        int ano,

        @NotNull @Min(1) @Max(12)
        int mes,
        @NotNull @Min(1) @Max(7)
        int dia_da_semana,

        @NotNull @Min(1) @Max(31)
        int dia,

        @NotEmpty
        List<DadosIndicadoresDia.IndicadorDiario> indicadores
) {
    public record IndicadorDiario(
            @NotBlank
            String origem,

            @NotNull @PositiveOrZero
            int quantidade
    ) {}
}






