package br.jus.tjrj.indicadores_disponibilidade_pje.controller;


import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHora;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHoraMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadoresDia;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosMediaMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.service.IndicadorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
@RequestMapping("/indicadores")
public class IndicadorDisponibilidadeController {

    @Autowired
    private IndicadorService indicadorService;

    @GetMapping("/allMonth/{ano}")
    @Operation(summary = "Obter todos os indicadores de um ano específico por mês",
            description = "Retorna os dados do indicador para uma determinado ano por mês")
    public ResponseEntity<List<DadosMediaMes>> obterMediaMensalAno(@PathVariable
                                                                       @Min(2000) @Max(2050) int ano) {

       List<DadosMediaMes> mediasMensaisMes = indicadorService.obterMediasMensaisPorAno(ano);

        return ResponseEntity.ok(mediasMensaisMes);
    }

    @GetMapping("/allDays/{ano}")
    @Operation(summary = "Obter todos os indicadores de um ano específico por dia",
            description = "Retorna os dados do indicador para uma determinado ano por dia")
    public ResponseEntity<List<DadosIndicadoresDia>> obterIndicadoresPorDia(@PathVariable
                                                                                @Min(2000) @Max(2050) int ano) {

        List<DadosIndicadoresDia> indicadoresPorDia = indicadorService.obterIndicadoresPorDia(ano);

        return ResponseEntity.ok(indicadoresPorDia);
    }


    @GetMapping("/{origem}")
    @Operation(summary = "Obter detalhe por hora de um indicador por dia específico",
            description = "Retorna os dados de hora de um indicador para uma determinado dia")
    public ResponseEntity<DadosIndicadorHora> obterDetalheHoraDeIndicador(
            @PathVariable Origem.OrigemEnum origem,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        DadosIndicadorHora IndicadorPorDiaHoraEOrigem =
                indicadorService.obterIndicadorPorDiaHoraEOrigem(data, origem);

        return ResponseEntity.ok(IndicadorPorDiaHoraEOrigem);
    }

    @GetMapping ("/{origem}/{mes}")
    @Operation(summary = "Obter detalhe por hora de um indicador por mês específico",
            description = "Retorna os dados de hora de um indicador para uma determinado mês")
    public ResponseEntity<DadosIndicadorHoraMes> obterDetalheHoraMesdeIndicador (
            @PathVariable Origem.OrigemEnum origem,
            @PathVariable @Min(1) @Max(12) int mes,
            @RequestParam @Min(2000) @Max(2050) int ano
    ){
        DadosIndicadorHoraMes IndicadorPorMesHoraEOrigem =
                indicadorService.obterIndicadorPorMesHoraEOrigem(origem,mes,ano);

        return ResponseEntity.ok(IndicadorPorMesHoraEOrigem);
    }



}





