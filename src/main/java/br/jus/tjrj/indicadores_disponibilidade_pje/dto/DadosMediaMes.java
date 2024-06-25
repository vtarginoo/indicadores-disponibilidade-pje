package br.jus.tjrj.indicadores_disponibilidade_pje.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public record DadosMediaMes(

        @Schema(
                description = "Ano dos dados",
                example = "2024",
                minimum = "2010",
                maximum = "2030"
        )
        @NotNull @Min(2010) @Max(2030)
        int ano,

        @Schema(description = "Mês dos dados", example = "1", minimum = "1", maximum = "12")
        @NotNull @Min(1) @Max(12)
        int mes,

        @Schema(description = "Lista de indicadores mensais")
        @NotEmpty
        List<IndicadorMensal> indicadores
) {

    @Schema(
            oneOf = IndicadorMensal.class,
            example = "{\"origem\": \"distribuicao\", \"media\": 196.39}" // String JSON válida
    )
    public record IndicadorMensal(
            @Schema(description = "Origem do indicador", example = "distribuicao")
            @NotBlank
            String origem,

            @Schema(description = "Média do indicador", example = "196.39")
            @NotNull @PositiveOrZero
            double media
    ) {}

}