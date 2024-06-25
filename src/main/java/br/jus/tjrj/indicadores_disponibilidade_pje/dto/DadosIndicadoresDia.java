package br.jus.tjrj.indicadores_disponibilidade_pje.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(
        description = "Dados dos indicadores por dia",
        example = """
            {
              "ano": 2024,
              "mes": 6,
              "dia_da_semana": 2,
              "dia": 25,
              "indicadores": [
                {
                  "origem": "distribuicao",
                  "quantidade": 350
                },
                {
                  "origem": "grerj_vinculada",
                  "quantidade": 520
                }
              ]
            }
            """
)
public record DadosIndicadoresDia(
        @Schema(description = "Ano dos dados", example = "2024", minimum = "1900", maximum = "2100")
        @NotNull @Min(1900) @Max(2100)
        int ano,

        @Schema(description = "Mês dos dados", example = "6", minimum = "1", maximum = "12")
        @NotNull @Min(1) @Max(12)
        int mes,

        @Schema(description = "Dia da semana (1 = Domingo, 7 = Sábado)", example = "2", minimum = "1", maximum = "7")
        @NotNull @Min(1) @Max(7)
        int dia_da_semana,

        @Schema(description = "Dia do mês", example = "25", minimum = "1", maximum = "31")
        @NotNull @Min(1) @Max(31)
        int dia,

        @Schema(description = "Lista de indicadores diários")
        @NotEmpty
        List<DadosIndicadoresDia.IndicadorDiario> indicadores
) {

    @Schema(description = "Indicador diário")
    public record IndicadorDiario(
            @Schema(description = "Origem do indicador", example = "distribuicao")
            @NotBlank
            String origem,

            @Schema(description = "Quantidade", example = "350", minimum = "0")
            @NotNull @PositiveOrZero
            int quantidade
    ) {}
}






