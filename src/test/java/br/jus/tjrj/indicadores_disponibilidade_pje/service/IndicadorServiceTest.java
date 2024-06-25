package br.jus.tjrj.indicadores_disponibilidade_pje.service;

import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosIndicadoresDia;
import br.jus.tjrj.indicadores_disponibilidade_pje.dto.DadosMediaMes;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.IndicadoresDiaProjection;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.MediaIndicadorPorMesProjection;
import br.jus.tjrj.indicadores_disponibilidade_pje.repository.IndicadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorServiceTest {

    @InjectMocks
    private IndicadorService service;

    @Mock
    private IndicadorRepository repository;

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





}







