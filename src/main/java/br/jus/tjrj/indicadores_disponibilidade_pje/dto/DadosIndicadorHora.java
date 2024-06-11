package br.jus.tjrj.indicadores_disponibilidade_pje.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record DadosIndicadorHora(
        @NotNull @Min(1900) @Max(2100)
        int ano,

        @NotNull @Min(1) @Max(12)
        int mes,

        @NotNull @Min(1) @Max(31)
        int dia,

        @NotNull @Min(1) @Max(7)
        int dia_da_semana,

        @NotNull
        int origem,

        @NotEmpty
        List<DadosIndicadorHora.IndicadorHora> indicador)
     {
        public record IndicadorHora(

                @NotNull @Min(0) @Max(23)
                int hora,

                @NotNull @Min(0)
                int quantidade
        ) {}

    }


