package br.jus.tjrj.indicadores_disponibilidade_pje.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.List;


@Schema(
        description = "Dados do indicador por hora",
        example = """
            {
              "ano": 2024,
              "mes": 6,
              "dia": 25,
              "dia_da_semana": 2,
              "origem": 0,
              "indicador": [
                {
                  "hora": 10,
                  "quantidade": 150
                },
                {
                  "hora": 14,
                  "quantidade": 200
                }
              ]
            }
            """
)
public record DadosIndicadorHora(
        @Schema(description = "Ano dos dados", example = "2024", minimum = "1900", maximum = "2030")
        @NotNull @Min(2010) @Max(2030)
        int ano,

        @Schema(description = "Mês dos dados", example = "6", minimum = "1", maximum = "12")
        @NotNull @Min(1) @Max(12)
        int mes,

        @Schema(description = "Dia dos dados", example = "25", minimum = "1", maximum = "31")
        @NotNull @Min(1) @Max(31)
        int dia,

        @Schema(description = "Dia da semana (1 = Domingo, 7 = Sábado)", example = "2", minimum = "1", maximum = "7")
        @NotNull @Min(1) @Max(7)
        int dia_da_semana,

        @Schema(description = "Origem dos dados", example = "1")
        @NotNull
        int origem,

        @Schema(description = "Lista de indicadores por hora")
        @NotEmpty
        List<DadosIndicadorHora.IndicadorHora> indicador)
     {
         @Schema(description = "Indicador por hora")
         public record IndicadorHora(
                 @Schema(description = "Hora do dia (0-23)", example = "10", minimum = "0", maximum = "23")
                 @NotNull @Min(0) @Max(23)
                 @Getter
                 int hora,

                 @Schema(description = "Quantidade", example = "150", minimum = "0")
                 @NotNull @Min(0)
                 @Getter
                 int quantidade
         ) {}

    }


