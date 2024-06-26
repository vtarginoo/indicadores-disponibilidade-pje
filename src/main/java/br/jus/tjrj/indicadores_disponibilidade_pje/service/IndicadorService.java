package br.jus.tjrj.indicadores_disponibilidade_pje.service;

import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHora;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHoraMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadoresDia;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosMediaMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.IndicadorDisponibilidade;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.IndicadorHoraMesProjection;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.IndicadoresDiaProjection;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.MediaIndicadorPorMesProjection;
import br.jus.tjrj.indicadores_disponibilidade_pje.repository.IndicadorRepository;
import br.jus.tjrj.indicadores_disponibilidade_pje.repository.OrigemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class IndicadorService {

    @Autowired
    private IndicadorRepository repository;

    @Autowired
    private OrigemRepository origemRepository;

    //AllMonth
    public List<DadosMediaMes> obterMediasMensaisPorAno(int ano) {

        List<MediaIndicadorPorMesProjection> somaQuantidadeMesEOrigem = repository.findMediaIndicadorPorMes(ano);

        if (somaQuantidadeMesEOrigem.isEmpty()) {
            throw new EntityNotFoundException("Não há dados disponíveis para o ano especificado.");
        }

        Map<Integer, List<DadosMediaMes.IndicadorMensal>> indicadoresPorMes = somaQuantidadeMesEOrigem.stream()
                .collect(Collectors.groupingBy(MediaIndicadorPorMesProjection::getMes,
                        Collectors.mapping(
                                p -> {
                                    YearMonth yearMonth = YearMonth.of(ano, p.getMes());
                                    double mediaDiaria = Math.round(
                                            (p.getSomaQuantidade().doubleValue() / yearMonth.lengthOfMonth()) * 100.0) / 100.0;
                                    return new DadosMediaMes.IndicadorMensal(p.getOrigem(), mediaDiaria);
                                },
                                Collectors.toList()
                        )
                ));

        return indicadoresPorMes.entrySet().stream()
                .map(entry -> new DadosMediaMes(ano, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // AllDays
    public List<DadosIndicadoresDia> indicadoresPorDia(int ano) {
        List<IndicadoresDiaProjection> resultados = repository.findIndicadoresPorDiaAgrupados(ano);


        if (resultados.isEmpty()) {
            throw new EntityNotFoundException("Não há dados disponíveis para o período especificado.");
        }



        Map<LocalDate, List<DadosIndicadoresDia.IndicadorDiario>> indicadoresPorData = resultados.stream()
                .collect(Collectors.groupingBy(
                        IndicadoresDiaProjection::getData,
                        Collectors.mapping(
                                p -> new DadosIndicadoresDia.IndicadorDiario(p.getOrigem().name(),
                                        p.getQuantidade()),
                                Collectors.toList()
                        )
                ));


        return indicadoresPorData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Ordena pelo LocalDate (chave do mapa)
                .map(entry -> {
                    LocalDate data = entry.getKey();
                    List<DadosIndicadoresDia.IndicadorDiario> indicadores = entry.getValue();
                    return new DadosIndicadoresDia(data.getYear(), data.getMonthValue(),
                            data.getDayOfWeek().getValue(), data.getDayOfMonth(), indicadores);
                })
                .collect(Collectors.toList());}



    public DadosIndicadorHora obterIndicadorPorDiaHoraEOrigem(LocalDate data, Origem.OrigemEnum origem) {

        List<IndicadorDisponibilidade> indicadores = repository.findByOrigemOrigemAndData(origem, data);

        if (indicadores.isEmpty()) {
            throw new EntityNotFoundException("Não há dados disponíveis para a data e origem especificadas.");
        }

        List<DadosIndicadorHora.IndicadorHora> indicadoresHora = indicadores.stream()
                .map(indicador -> new DadosIndicadorHora.IndicadorHora(indicador.getHora(), indicador.getQuantidade()))
                // Ordenação por hora em ordem crescente:
                .sorted(Comparator.comparingInt(DadosIndicadorHora.IndicadorHora::getHora))
                .collect(Collectors.toList());

        IndicadorDisponibilidade primeiroIndicador = indicadores.get(0);
        Origem origemIndicador = primeiroIndicador.getOrigem();

        return new DadosIndicadorHora(
                data.getYear(),
                data.getMonthValue(),
                data.getDayOfMonth(),
                primeiroIndicador.getDiaDaSemana(),
                origemIndicador.getId().intValue(),
                indicadoresHora
        );
    }



    public DadosIndicadorHoraMes obterIndicadorPorMesHoraEOrigem(Origem.OrigemEnum origemEnum, int mes, int ano) {
        Origem origemEntity = origemRepository.findByOrigem(origemEnum)
                .orElseThrow(() -> new EntityNotFoundException("Não há dados disponíveis para a data e origem especificadas."));

        List<IndicadorHoraMesProjection> resultados = repository.findByOrigemEMesEAno(origemEnum, mes, ano);

        if (resultados.isEmpty()) {
            throw new EntityNotFoundException("Não há dados para o período: " + origemEnum);
        }

        List<DadosIndicadorHoraMes.IndicadorHoraMes> indicadores = resultados.stream()
                .map(p -> new DadosIndicadorHoraMes.IndicadorHoraMes(p.getHora(), p.getMedia()))
                .toList();

        return new DadosIndicadorHoraMes(ano, mes, origemEnum.name(), indicadores);


    }

    public List<DadosIndicadorHora> obterIndicadoresComparacao(Origem.OrigemEnum origem, LocalDate data) {

        List<DadosIndicadorHora> indicadores = new ArrayList<>();

        // Obter dados para a data de referência
        DadosIndicadorHora dadosReferencia = obterIndicadorPorDiaHoraEOrigem(data, origem);
        indicadores.add(dadosReferencia);

        // Obter dados para 7 dias antes da data de referência
        LocalDate dataMenos7 = data.minusDays(7);
        DadosIndicadorHora dadosMenos7 = obterIndicadorPorDiaHoraEOrigem(dataMenos7, origem);
        indicadores.add(dadosMenos7);

        // Obter dados para 14 dias antes da data de referência
        LocalDate dataMenos14 = data.minusDays(14);
        DadosIndicadorHora dadosMenos14 = obterIndicadorPorDiaHoraEOrigem(dataMenos14, origem);
        indicadores.add(dadosMenos14);

        return indicadores;
    }
}










