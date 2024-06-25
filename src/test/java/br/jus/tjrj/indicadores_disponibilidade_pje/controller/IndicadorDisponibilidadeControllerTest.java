package br.jus.tjrj.indicadores_disponibilidade_pje.controller;

import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.service.IndicadorService;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
class IndicadorDisponibilidadeControllerTest {

    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private IndicadorDisponibilidadeController controller;

    @MockBean
    private IndicadorService service;


    @ParameterizedTest
    @ValueSource(ints = {2009, 2031}) // Valores inválidos: abaixo e acima do limite
    @DisplayName("allMonth - Deve retornar erro 400 quando o ano for inválido (abaixo ou acima do limite)")
    void AllMonthAnoInvalido(int anoInvalido) throws Exception {
        // ACT (Ação)
        ResultActions result = mvc.perform(get("/indicadores/allMonth/{ano}", anoInvalido));

        // ASSERT (Verificação)
        result.andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(ints = {2011, 2029})
    @DisplayName("allMonth - Deve retornar erro 404 quando não há dados disponíveis para o ano")
    void AllMonthAnoSemDados(int anoSemDados) throws Exception {
        // ARRANGE
        when(service.obterMediasMensaisPorAno(anoSemDados))
                .thenThrow(new EntityNotFoundException("Não há dados disponíveis para o ano especificado."));

        // ACT
        ResultActions result = mvc.perform(get("/indicadores/allMonth/{ano}", anoSemDados));

        // ASSERT (Verificação)
        result.andExpect(MockMvcResultMatchers.status().isNotFound()) // Use MockMvcResultMatchers
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        Matchers.is("Não há dados disponíveis para o ano especificado.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus",
                        Matchers.is("NOT_FOUND")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").isArray());
    }

    @ParameterizedTest
    @ValueSource(ints = {2009, 2031}) // Valores inválidos: abaixo e acima do limite
    @DisplayName("allDays - Deve retornar erro 400 quando o ano for inválido (abaixo ou acima do limite)")
    void AllDaysAnoInvalido(int anoInvalido) throws Exception {
        // ACT (Ação)
        ResultActions result = mvc.perform(get("/indicadores/allDays/{ano}", anoInvalido));

        // ASSERT (Verificação)
        result.andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(ints = {2011, 2029})
    @DisplayName("allDays - Deve retornar erro 404 quando não há dados disponíveis para o ano")
    void AllDaysAnoSemDados(int anoSemDados) throws Exception {
        // ARRANGE
        when(service.indicadoresPorDia(anoSemDados))
                .thenThrow(new EntityNotFoundException("Não há dados disponíveis para o ano especificado."));

        // ACT
        ResultActions result = mvc.perform(get("/indicadores/allDays/{ano}", anoSemDados));

        // ASSERT (Verificação)
        result.andExpect(MockMvcResultMatchers.status().isNotFound()) // Use MockMvcResultMatchers
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        Matchers.is("Não há dados disponíveis para o ano especificado.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus",
                        Matchers.is("NOT_FOUND")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").isArray());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando a data for anterior a 2010")
    void obterDetalheHoraDeIndicador_DataInvalidaAnterior() throws Exception {
        LocalDate dataInvalida = LocalDate.of(2009, 12, 31);
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;

        mvc.perform(get("/indicadores/{origem}", origem)
                        .param("data", dataInvalida.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        is("A data selecionada deverá ser superior à 2010.")));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando a data for futura")
    void obterDetalheHoraDeIndicador_DataInvalidaFutura() throws Exception {
        LocalDate dataFutura = LocalDate.now().plusDays(1);
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;

        mvc.perform(get("/indicadores/{origem}", origem)
                        .param("data", dataFutura.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        is("A data selecionada deverá ser a de hoje ou passada.")));
    }
    @Test
    @DisplayName("Deve retornar erro 400 quando a origem for inválida")
    void obterDetalheHoraDeIndicador_OrigemInvalida() throws Exception {
        String origemInvalida = "origem_inexistente";
        LocalDate data = LocalDate.of(2023, 6, 15); // Data válida

        mvc.perform(get("/indicadores/{origem}", origemInvalida)
                        .param("data", data.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof MethodArgumentTypeMismatchException));
        // Verifica se a exceção lançada é do tipo correto
    }

    @Test
    @DisplayName("Deve retornar erro 404 quando não há dados para a data e origem especificadas")
    void obterDetalheHoraDeIndicador_DadosNaoEncontrados() throws Exception {
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;
        LocalDate data = LocalDate.of(2023, 6, 15); // Data válida

        when(service.obterIndicadorPorDiaHoraEOrigem(data, origem))
                .thenThrow(new EntityNotFoundException("Não há dados disponíveis para a data e origem especificadas."));

        mvc.perform(get("/indicadores/{origem}", origem)
                        .param("data", data.toString()))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        is("Não há dados disponíveis para a data e origem especificadas.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus", is("NOT_FOUND")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").isArray());
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 13})
    @DisplayName("Deve retornar erro 400 quando o mês for inválido (fora do intervalo 1-12)")
    void obterDetalheHoraMesdeIndicador_MesInvalido(int mesInvalido) throws Exception {
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;
        int ano = 2023;

        mvc.perform(get("/indicadores/{origem}/{mes}", origem, mesInvalido)
                        .param("ano", String.valueOf(ano)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(ints = {2009, 2031})
    @DisplayName("Deve retornar erro 400 quando o ano for inválido (fora do intervalo 2010-2030)")
    void obterDetalheHoraMesdeIndicador_AnoInvalido(int anoInvalido) throws Exception {
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;
        int mes = 6;

        mvc.perform(get("/indicadores/{origem}/{mes}", origem, mes)
                        .param("ano", String.valueOf(anoInvalido)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Deve retornar erro 400 quando a origem for inválida")
    void obterDetalheHoraMesdeIndicador_OrigemInvalida() throws Exception {
        String origemInvalida = "origem_inexistente";
        int mes = 6;
        int ano = 2023;

        mvc.perform(get("/indicadores/{origem}/{mes}", origemInvalida, mes)
                        .param("ano", String.valueOf(ano)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro 404 quando não há dados para o mês, ano e origem especificados")
    void obterDetalheHoraMesdeIndicador_DadosNaoEncontrados() throws Exception {
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;
        int mes = 6;
        int ano = 2011;

        when(service.obterIndicadorPorMesHoraEOrigem(origem, mes, ano))
                .thenThrow(new EntityNotFoundException("Não há dados disponíveis para o mês, ano e origem especificados."));

        mvc.perform(get("/indicadores/{origem}/{mes}", origem, mes)
                        .param("ano", String.valueOf(ano)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        is("Não há dados disponíveis para o mês, ano e origem especificados.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus", is("NOT_FOUND")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").isArray());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando a data for anterior a 2010")
    void obterDetalhesComparacao_DataInvalidaAnterior() throws Exception {
        LocalDate dataInvalida = LocalDate.of(2009, 12, 31);
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;

        mvc.perform(get("/indicadores/{origem}/comparacao", origem)
                        .param("data", dataInvalida.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        is("A data selecionada deverá ser superior à 2010.")));
    }
    @Test
    @DisplayName("Deve retornar erro 400 quando a data for futura")
    void obterDetalhesComparacao_DataInvalidaFutura() throws Exception {
        LocalDate dataFutura = LocalDate.now().plusDays(1);
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;

        mvc.perform(get("/indicadores/{origem}/comparacao", origem)
                        .param("data", dataFutura.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        is("A data selecionada deverá ser a de hoje ou passada.")));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando a origem for inválida")
    void obterDetalhesComparacao_OrigemInvalida() throws Exception {
        String origemInvalida = "origem_inexistente";
        LocalDate data = LocalDate.of(2023, 6, 15); // Data válida

        mvc.perform(get("/indicadores/{origem}/comparacao", origemInvalida)
                        .param("data", data.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof MethodArgumentTypeMismatchException));
    }


    @Test
    @DisplayName("Deve retornar erro 404 quando não há dados para a data e origem especificadas")
    void obterDetalhesComparacao_DadosNaoEncontrados() throws Exception {
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;
        LocalDate data = LocalDate.of(2023, 6, 15); // Data válida

        when(service.obterIndicadoresComparacao(origem, data))
                .thenThrow(new EntityNotFoundException("Não há dados disponíveis para a data e origem especificadas."));

        mvc.perform(get("/indicadores/{origem}/comparacao", origem)
                        .param("data", data.toString()))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        is("Não há dados disponíveis para a data e origem especificadas.")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus", is("NOT_FOUND")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").isArray());
    }

    //// Testes de Sucesso código 200


    @Test
    @DisplayName("All Month -Deve retornar os dados dos indicadores por mês para o ano especificado")
    void AllMonth_Sucesso() throws Exception {
        int ano = 2024;

        // ACT (Ação)
        ResultActions result = mvc.perform(get("/indicadores/allMonth/{ano}", ano));

        // ASSERT (Verificação)
        result.andExpect(MockMvcResultMatchers.status().isOk()) // Use MockMvcResultMatchers
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("All Days - Deve retornar os dados dos indicadores por mês para o ano especificado")
    void AllDays_Sucesso() throws Exception {
        int ano = 2024;

        // ACT (Ação)
        ResultActions result = mvc.perform(get("/indicadores/allDays/{ano}", ano));

        // ASSERT (Verificação)
        result.andExpect(MockMvcResultMatchers.status().isOk()) // Use MockMvcResultMatchers
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Deve retornar os dados do indicador por hora para o dia especificado")
    void obterDetalheHoraDeIndicador_Sucesso() throws Exception {
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;
        String data = "2024-02-02";

        ResultActions result = mvc.perform(get("/indicadores/{origem}", origem)
                        .param("data", data));

       // ASSERT (Verificação)
        result.andExpect(MockMvcResultMatchers.status().isOk()) // Use MockMvcResultMatchers
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    @DisplayName("Deve retornar os dados do indicador por hora para o mes especificado")
    void obterDetalheHoraMesdeIndicador_Sucesso() throws Exception {
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;
        int mes = 1;
        int ano = 2024;

        var result = mvc.perform(get("/indicadores/{origem}/{mes}", origem,mes)
                .param("ano", String.valueOf(ano)));

        // ASSERT (Verificação)
        result.andExpect(MockMvcResultMatchers.status().isOk()) // Use MockMvcResultMatchers
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    @DisplayName("Deve retornar os dados do indicador por hora para o dia especificado")
    void obterDetalheComparacao_Sucesso() throws Exception {
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao;
        String data = "2024-02-02";

        ResultActions result = mvc.perform(get("/indicadores/{origem}/comparacao", origem)
                .param("data", data));

        // ASSERT (Verificação)
        result.andExpect(MockMvcResultMatchers.status().isOk()) // Use MockMvcResultMatchers
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }


    }