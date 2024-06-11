package br.jus.tjrj.indicadores_disponibilidade_pje.service;

import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHora;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHoraMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadoresDia;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosMediaMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.IndicadorDisponibilidade;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.repository.IndicadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndicadorService {

    @Autowired
    private IndicadorRepository repository;

    public List<DadosMediaMes> obterMediasMensaisPorAno(int ano) {

        // 1. Obter a soma das quantidades por mês e origem
        List<Object[]> MediaIndicadorPorMes= repository.findMediaIndicadorPorMes(ano);

        // 2. Agrupar por mês
        Map<Integer, List<DadosMediaMes.IndicadorMensal>> indicadoresPorMes = MediaIndicadorPorMes.stream()
                .collect(Collectors.groupingBy(
                        r -> (Integer) r[0], // Agrupa pelo mês
                        Collectors.mapping(
                                r -> {
                                    String origem = String.valueOf(r[1]);
                                    int mesNumero = (Integer) r[0];
                                    YearMonth yearMonth = YearMonth.of(ano, mesNumero);
                                    double mediaDiaria = Math.round(((Long) r[2]).doubleValue() / yearMonth.lengthOfMonth() * 100.0) / 100.0;
                                    return new DadosMediaMes.IndicadorMensal(origem, mediaDiaria);
                                },
                                Collectors.toList()
                        )
                ));

        // 3. Formatar o resultado
        List<DadosMediaMes> resultado = indicadoresPorMes.entrySet().stream()
                .map(entry -> new DadosMediaMes(ano, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return resultado;
    }


    public List<DadosIndicadoresDia> obterIndicadoresPorDia(int ano) {
        List<Object[]> resultados = repository.findIndicadoresPorDiaAgrupados(ano);

        Map<LocalDate, List<DadosIndicadoresDia.IndicadorDiario>> indicadoresPorData = resultados.stream()
                .collect(Collectors.groupingBy(
                        resultado -> (LocalDate) resultado[0],
                        Collectors.mapping(
                                resultado -> new DadosIndicadoresDia.IndicadorDiario((String) resultado[3], ((Number) resultado[4]).intValue()),
                                Collectors.toList()
                        )
                ));

        return indicadoresPorData.entrySet().stream()
                .map(entry -> {
                    LocalDate data = entry.getKey();
                    List<DadosIndicadoresDia.IndicadorDiario> indicadores = entry.getValue();
                    return new DadosIndicadoresDia(data.getYear(), data.getMonthValue(),
                            data.getDayOfWeek().getValue(), data.getDayOfMonth(), indicadores);
                })
                .collect(Collectors.toList());
    }


    public DadosIndicadorHora obterIndicadorPorDiaHoraEOrigem(LocalDate data, String origem) {
        List<IndicadorDisponibilidade> indicadores = repository.findByOrigemAndData(origem, data);

        if (indicadores.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Indicadores não encontrados para a data e origem especificadas.");
        }

        List<DadosIndicadorHora.IndicadorHora> indicadoresHora = indicadores.stream()
                .map(indicador -> new DadosIndicadorHora.IndicadorHora(indicador.getHora(), indicador.getQuantidade()))
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

    public DadosIndicadorHoraMes obterIndicadorPorMesHoraEOrigem(String nomeOrigem, int mes, int ano) {
        List<Object[]> resultados = repository.findByOrigemEMesEAno(nomeOrigem, mes, ano);

        if (resultados.isEmpty()) {
            throw new IllegalArgumentException("Origem não encontrada ou não há dados para o período: " + nomeOrigem);
        }

        List<DadosIndicadorHoraMes.IndicadorHoraMes> indicadores = resultados.stream()
                .map(resultado -> {
                    double mediaOriginal = (double) resultado[4];
                    double mediaFormatada = Math.round(mediaOriginal * 100.0) / 100.0; // Arredonda para 2 casas decimais
                    return new DadosIndicadorHoraMes.IndicadorHoraMes((int) resultado[3], mediaFormatada);
                })
                .toList();

        String origem = (String) resultados.get(0)[2];

        return new DadosIndicadorHoraMes(ano, mes, origem, indicadores);
    }
}










