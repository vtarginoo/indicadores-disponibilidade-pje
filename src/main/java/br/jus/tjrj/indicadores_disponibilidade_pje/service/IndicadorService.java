package br.jus.tjrj.indicadores_disponibilidade_pje.service;

import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHora;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadorHoraMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadoresDia;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosMediaMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.IndicadorDisponibilidade;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.repository.IndicadorRepository;
import br.jus.tjrj.indicadores_disponibilidade_pje.repository.OrigemRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Autowired
    private OrigemRepository origemRepository;

    public List<DadosMediaMes> obterMediasMensaisPorAno(int ano) {

        // 1. Obter a soma das quantidades por mês e origem
        List<Object[]> MediaIndicadorPorMes = repository.findMediaIndicadorPorMes(ano);

        if (MediaIndicadorPorMes.isEmpty()) {
            throw new EntityNotFoundException("Não há dados disponíveis para o ano especificado.");
        }

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

        if (resultados.isEmpty()) {
            throw new EntityNotFoundException("Não há dados disponíveis para o período especificado.");
        }

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


    public DadosIndicadorHora obterIndicadorPorDiaHoraEOrigem(LocalDate data, Origem.OrigemEnum origem) {
        List<IndicadorDisponibilidade> indicadores = repository.findByOrigemAndData(origem, data);


        if (indicadores.isEmpty()) {
            throw new EntityNotFoundException("Não há dados disponíveis para a data e origem especificadas.");
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

    public DadosIndicadorHoraMes obterIndicadorPorMesHoraEOrigem(Origem.OrigemEnum origemEnum, int mes, int ano) {
        // 1. Buscar a entidade Origem pelo valor do enum (opcional, mas recomendado)
        Origem origemEntity = origemRepository.findByOrigem(origemEnum)
                .orElseThrow(() -> new EntityNotFoundException("Origem não encontrada: " + origemEnum));

        // 2. Obter os resultados da consulta (usando a entidade Origem ou o enum diretamente)
        List<Object[]> resultados = repository.findByOrigemEMesEAno(origemEnum, mes, ano);

        // 3. Verificar se há resultados
        if (resultados.isEmpty()) {
            throw new EntityNotFoundException("Não há dados para o período: " + origemEnum);
        }

        // 4. Mapear os resultados para o DTO
        List<DadosIndicadorHoraMes.IndicadorHoraMes> indicadores = resultados.stream()
                .map(resultado -> new DadosIndicadorHoraMes.IndicadorHoraMes((int) resultado[3], (double) resultado[4]))
                .toList();

        // 5. Criar e retornar o DTO (usando o nome da origem do enum)
        return new DadosIndicadorHoraMes(ano, mes, origemEnum.name(), indicadores); // Usar o nome do enum
    }

}










