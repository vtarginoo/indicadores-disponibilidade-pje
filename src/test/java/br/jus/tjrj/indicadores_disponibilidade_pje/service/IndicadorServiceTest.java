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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class IndicadorServiceTest {

    @InjectMocks
    private IndicadorService service;

    @Mock
    private IndicadorRepository repository;

    @Mock
    private OrigemRepository origemRepository;

    @Test
    @DisplayName("AllMonth - Deve lançar EntityNotFoundException quando não há dados para o ano")
    void obterMediasMensaisPorAno_semDados() {
        // ARRANGE
        int ano = 2024;

        // Configure o mock para retornar uma lista vazia
        when(repository.findMediaIndicadorPorMes(ano)).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        // Verifica se a exceção é lançada
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.obterMediasMensaisPorAno(ano);
        });

        // Verifica a mensagem da exceção
        assertEquals("Não há dados disponíveis para o ano especificado.", exception.getMessage());

        // Verifica se o repositório foi chamado com o argumento correto
        verify(repository).findMediaIndicadorPorMes(ano);
    }

    @Test
    @DisplayName("AllMonth - Deve retornar DadosMediaMes corretamente a partir de MediaIndicadorPorMesProjection")
    void obterMediasMensaisPorAno_comDados() {
        // ARRANGE
        int ano = 2024;
        // Mock para o mês 1 com duas origens diferentes
        MediaIndicadorPorMesProjection projecao1 = mock(MediaIndicadorPorMesProjection.class);
        when(projecao1.getMes()).thenReturn(1);
        when(projecao1.getOrigem()).thenReturn("Origem A");
        when(projecao1.getSomaQuantidade()).thenReturn(150L);

        MediaIndicadorPorMesProjection projecao2 = mock(MediaIndicadorPorMesProjection.class);
        when(projecao2.getMes()).thenReturn(1);
        when(projecao2.getOrigem()).thenReturn("Origem B");
        when(projecao2.getSomaQuantidade()).thenReturn(200L);

        // Mock para o mês 2 com uma origem
        MediaIndicadorPorMesProjection projecao3 = mock(MediaIndicadorPorMesProjection.class);
        when(projecao3.getMes()).thenReturn(2);
        when(projecao3.getOrigem()).thenReturn("Origem A");
        when(projecao3.getSomaQuantidade()).thenReturn(100L);

        // Juntando os mocks em uma lista
        List<MediaIndicadorPorMesProjection> projecoes = Arrays.asList(projecao1, projecao2, projecao3);

        when(repository.findMediaIndicadorPorMes(ano)).thenReturn(projecoes);

        // ACT
        List<DadosMediaMes> resultado = service.obterMediasMensaisPorAno(ano);

        // ASSERT
        assertEquals(2, resultado.size()); // Dois meses distintos

        // Verificações para o mês 1
        DadosMediaMes mes1 = resultado.get(0);
        assertEquals(ano, mes1.ano());
        assertEquals(1, mes1.mes());
        assertEquals(2, mes1.indicadores().size()); // Duas origens distintas para o mês 1

        // Verificações para o mês 2
        DadosMediaMes mes2 = resultado.get(1);
        assertEquals(ano, mes2.ano());
        assertEquals(2, mes2.mes());
        assertEquals(1, mes2.indicadores().size()); // Uma origem para o mês 2

    }



    @Test
    @DisplayName("AllDays - Deve lançar EntityNotFoundException quando não há dados para o ano")
    void indicadoresPorDia_semDados() {
        // ARRANGE
        int ano = 2024;

        // Configure o mock para retornar uma lista vazia
        when(repository.findIndicadoresPorDiaAgrupados(ano)).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        // Verifica se a exceção é lançada
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            service.indicadoresPorDia(ano);
        });

        // Verifica a mensagem da exceção
        assertEquals("Não há dados disponíveis para o período especificado.", exception.getMessage());

        // Verifica se o repositório foi chamado com o argumento correto
        verify(repository).findIndicadoresPorDiaAgrupados(ano);
    }


    @Test
    @DisplayName("AllDays - Deve retornar DadosIndicadoresDia corretamente a partir de IndicadoresDiaProjection")
    void indicadoresPorDia_comDados() {
        // ARRANGE
        int ano = 2024;

        // Mocks de IndicadoresDiaProjection
        IndicadoresDiaProjection projecao1 = mock(IndicadoresDiaProjection.class);
        when(projecao1.getData()).thenReturn(LocalDate.of(2024, 6, 15));
        when(projecao1.getOrigem()).thenReturn(Origem.OrigemEnum.distribuicao);
        when(projecao1.getQuantidade()).thenReturn(10);

        IndicadoresDiaProjection projecao2 = mock(IndicadoresDiaProjection.class);
        when(projecao2.getData()).thenReturn(LocalDate.of(2024, 6, 15));
        when(projecao2.getOrigem()).thenReturn(Origem.OrigemEnum.grerj_vinculada);
        when(projecao2.getQuantidade()).thenReturn(25);

        IndicadoresDiaProjection projecao3 = mock(IndicadoresDiaProjection.class);
        when(projecao3.getData()).thenReturn(LocalDate.of(2024, 6, 16));
        when(projecao3.getOrigem()).thenReturn(Origem.OrigemEnum.distribuicao);
        when(projecao3.getQuantidade()).thenReturn(5);

        List<IndicadoresDiaProjection> projecoes = Arrays.asList(projecao1, projecao2, projecao3);
        when(repository.findIndicadoresPorDiaAgrupados(ano)).thenReturn(projecoes);

        // ACT
        List<DadosIndicadoresDia> resultado = service.indicadoresPorDia(ano);

        // ASSERT
        assertEquals(2, resultado.size()); // Verifica se retornou dois DadosIndicadoresDia

        // Verificações para o dia 15 de junho
        DadosIndicadoresDia dia15 = resultado.get(0);
        assertEquals(2024, dia15.ano()); // Verifica o ano
        assertEquals(6, dia15.mes());   // Verifica o mês
        assertEquals(6, dia15.dia_da_semana()); // Verifica o dia da semana
        assertEquals(15, dia15.dia());   // Verifica o dia do mês

        List<DadosIndicadoresDia.IndicadorDiario> indicadoresDia15 = dia15.indicadores();
        assertEquals(2, indicadoresDia15.size()); // Verifica se há dois indicadores para o dia 15

        // Verifica o primeiro indicador do dia 15
        DadosIndicadoresDia.IndicadorDiario indicador1Dia15 = indicadoresDia15.get(0);
        assertEquals("distribuicao", indicador1Dia15.origem());
        assertEquals(10, indicador1Dia15.quantidade());

        // Verifica o segundo indicador do dia 15
        DadosIndicadoresDia.IndicadorDiario indicador2Dia15 = indicadoresDia15.get(1);
        assertEquals("grerj_vinculada", indicador2Dia15.origem());
        assertEquals(25, indicador2Dia15.quantidade());

        // Verificações para o dia 16 de junho
        DadosIndicadoresDia dia16 = resultado.get(1);
        assertEquals(2024, dia16.ano());
        assertEquals(6, dia16.mes());
        assertEquals(7, dia16.dia_da_semana());
        assertEquals(16, dia16.dia());

        List<DadosIndicadoresDia.IndicadorDiario> indicadoresDia16 = dia16.indicadores();
        assertEquals(1, indicadoresDia16.size()); // Verifica se há um indicador para o dia 16

        // Verifica o indicador do dia 16
        DadosIndicadoresDia.IndicadorDiario indicadorDia16 = indicadoresDia16.get(0);
        assertEquals("distribuicao", indicadorDia16.origem());
        assertEquals(5, indicadorDia16.quantidade());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando não há dados para o período e origem especificados")
    void obterIndicadorPorDiaHoraEOrigem_semDados() {
        // ARRANGE (Preparação)
        LocalDate data = LocalDate.of(2024, 6, 26); // Data de hoje
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;

        // Configuração do mock do repositório para retornar uma lista vazia
        when(repository.findByOrigemOrigemAndData(origem, data)).thenReturn(Collections.emptyList());

        // ACT & ASSERT (Ação e Verificação)
        // Verificamos se a exceção EntityNotFoundException é lançada
        Throwable exception = assertThrows(EntityNotFoundException.class, () -> {
            service.obterIndicadorPorDiaHoraEOrigem(data, origem);
        });

        // Verificamos a mensagem da exceção
        assertEquals("Não há dados disponíveis para a data e origem especificadas.", exception.getMessage());
    }


    @Test
    @DisplayName("Deve retornar DadosIndicadorHora corretamente a partir de IndicadorDisponibilidade")
    void obterIndicadorPorDiaHoraEOrigem_comDados() {
        // ARRANGE
        LocalDate data = LocalDate.of(2024, 6, 15);
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;

        // Mocks de IndicadorDisponibilidade
        IndicadorDisponibilidade indicador1 = mock(IndicadorDisponibilidade.class);
        when(indicador1.getHora()).thenReturn(10);
        when(indicador1.getQuantidade()).thenReturn(150);
        when(indicador1.getDiaDaSemana()).thenReturn(6); // Sábado
        when(indicador1.getOrigem()).thenReturn(new Origem(1L, origem, "desc"));

        IndicadorDisponibilidade indicador2 = mock(IndicadorDisponibilidade.class);
        when(indicador2.getHora()).thenReturn(14);
        when(indicador2.getQuantidade()).thenReturn(80);

        IndicadorDisponibilidade indicador3 = mock(IndicadorDisponibilidade.class);
        when(indicador3.getHora()).thenReturn(12);
        when(indicador3.getQuantidade()).thenReturn(32);


        List<IndicadorDisponibilidade> indicadores = Arrays.asList(indicador1, indicador2, indicador3);

        when(repository.findByOrigemOrigemAndData(origem, data)).thenReturn(indicadores);

        // ACT
        DadosIndicadorHora resultado = service.obterIndicadorPorDiaHoraEOrigem(data, origem);

        // ASSERT
        assertEquals(2024, resultado.ano());
        assertEquals(6, resultado.mes());
        assertEquals(15, resultado.dia());
        assertEquals(6, resultado.dia_da_semana()); // Sábado
        assertEquals(1, resultado.origem()); // ID da origem

        List<DadosIndicadorHora.IndicadorHora> indicadoresHora = resultado.indicador();
        assertEquals(3, indicadoresHora.size());

        DadosIndicadorHora.IndicadorHora hora1 = indicadoresHora.get(0);
        assertEquals(10, hora1.hora());
        assertEquals(150, hora1.quantidade());

        DadosIndicadorHora.IndicadorHora hora2 = indicadoresHora.get(1);
        assertEquals(14, hora2.hora());
        assertEquals(80, hora2.quantidade());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando não há dados para o período e origem especificados")
    void obterIndicadorPorMesHoraEOrigem_semDados() {
        // ARRANGE
        int ano = 2024;
        int mes = 6;
        Origem.OrigemEnum origemEnum = Origem.OrigemEnum.distribuicao;

        when(repository.findByOrigemEMesEAno(origemEnum, mes, ano)).thenReturn(Collections.emptyList());

        // ACT & ASSERT
        assertThrows(EntityNotFoundException.class, () -> {
            service.obterIndicadorPorMesHoraEOrigem(origemEnum, mes, ano);
        }, "Não há dados disponíveis para a data e origem especificadas.");
    }

    @Test
    @DisplayName("Deve retornar DadosIndicadorHoraMes corretamente a partir de IndicadorHoraMesProjection")
    void obterIndicadorPorMesHoraEOrigem_comDados() {
        // ARRANGE
        int ano = 2024;
        int mes = 6;
        Origem.OrigemEnum origemEnum = Origem.OrigemEnum.distribuicao;
        Origem origemEntity = new Origem(1L, origemEnum, "Descrição da Origem");

        // Mocks de IndicadorHoraMesProjection
        IndicadorHoraMesProjection projecao1 = mock(IndicadorHoraMesProjection.class);
        when(projecao1.getHora()).thenReturn(10);
        when(projecao1.getMedia()).thenReturn(150.5);

        IndicadorHoraMesProjection projecao2 = mock(IndicadorHoraMesProjection.class);
        when(projecao2.getHora()).thenReturn(14);
        when(projecao2.getMedia()).thenReturn(80.2);

        List<IndicadorHoraMesProjection> projecoes = Arrays.asList(projecao1, projecao2);
        when(repository.findByOrigemEMesEAno(origemEnum, mes, ano)).thenReturn(projecoes);

        when(origemRepository.findByOrigem(origemEnum)).thenReturn(Optional.of(origemEntity));

        // ACT
        DadosIndicadorHoraMes resultado = service.obterIndicadorPorMesHoraEOrigem(origemEnum, mes, ano);

        // ASSERT
        assertEquals(ano, resultado.ano());
        assertEquals(mes, resultado.mes());
        assertEquals(origemEnum.name(), resultado.origem());

        List<DadosIndicadorHoraMes.IndicadorHoraMes> indicadoresHoraMes = resultado.indicador();
        assertEquals(2, indicadoresHoraMes.size());

        DadosIndicadorHoraMes.IndicadorHoraMes hora1 = indicadoresHoraMes.get(0);
        assertEquals(10, hora1.hora());
        assertEquals(150.5, hora1.media(), 0.01); // Tolerância para arredondamento

        DadosIndicadorHoraMes.IndicadorHoraMes hora2 = indicadoresHoraMes.get(1);
        assertEquals(14, hora2.hora());
        assertEquals(80.2, hora2.media(), 0.01); // Tolerância para arredondamento
    }












    }



















