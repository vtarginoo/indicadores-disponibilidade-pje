package br.jus.tjrj.indicadores_disponibilidade_pje.controller;


import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHora;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHoraMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadoresDia;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosMediaMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.service.IndicadorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
                                                                       @Min(2010) @Max(2030) int ano) {

       List<DadosMediaMes> mediasMensaisMes = indicadorService.obterMediasMensaisPorAno(ano);

        return ResponseEntity.ok(mediasMensaisMes);
    }

    //allDays
    @GetMapping("/allDays/{ano}")
    @Operation(summary = "Obter todos os indicadores de um ano específico por dia",
            description = "Retorna os dados do indicador para uma determinado ano por dia")
    public ResponseEntity<List<DadosIndicadoresDia>> obterIndicadoresPorDia(@PathVariable
                                                                                @Min(2010) @Max(2030) int ano) {

        List<DadosIndicadoresDia> indicadoresPorDia = indicadorService.indicadoresPorDia(ano);

        return ResponseEntity.ok(indicadoresPorDia);
    }


    @GetMapping("/{origem}")
    @Operation(summary = "Obter detalhe por hora de um indicador por dia específico",
            description = "Retorna os dados de hora de um indicador para uma determinado dia")
    public ResponseEntity<DadosIndicadorHora> obterDetalheHoraDeIndicador(
            @PathVariable Origem.OrigemEnum origem,
            @RequestParam @PastOrPresent(message = "A data selecionada deverá ser a de hoje ou passada.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        // Validação manual
        LocalDate dataMinima = LocalDate.of(2010, 1, 1);
        if (data.isBefore(dataMinima)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data selecionada deverá ser superior à 2010.");
        }

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
            @RequestParam @Min(2010) @Max(2030) int ano
    ){
        DadosIndicadorHoraMes IndicadorPorMesHoraEOrigem =
                indicadorService.obterIndicadorPorMesHoraEOrigem(origem,mes,ano);

        return ResponseEntity.ok(IndicadorPorMesHoraEOrigem);
    }

    @GetMapping("/{origem}/comparacao")
    @Operation(summary = "Obter detalhes comparativos por hora de um indicador (D, D-7 e D-14)",
            description = "Retorna os dados por hora de um indicador para a data especificada, 7 dias antes e 14 dias antes.")
    public ResponseEntity<List<DadosIndicadorHora>> obterDetalhesComparacao(
            @PathVariable Origem.OrigemEnum origem,
            @RequestParam @PastOrPresent(message = "A data selecionada deverá ser a de hoje ou passada.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        // Validação manual
        LocalDate dataMinima = LocalDate.of(2010, 1, 1);
        if (data.isBefore(dataMinima)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data selecionada deverá ser superior à 2010.");
        }

        List<DadosIndicadorHora> indicadores = indicadorService.obterIndicadoresComparacao(origem, data);
        return ResponseEntity.ok(indicadores);
    }





}





