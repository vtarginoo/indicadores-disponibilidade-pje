package br.jus.tjrj.indicadores_disponibilidade_pje.repository;

import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.IndicadorHoraMesProjection;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.IndicadoresDiaProjection;
import br.jus.tjrj.indicadores_disponibilidade_pje.entity.projections.MediaIndicadorPorMesProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class IndicadorRepositoryTest {

    @Autowired
    private IndicadorRepository indicadorRepository;

    @Test
    @DisplayName("Deveria devolver uma lista vazia quando não há dados no ano")
    void findMediaIndicadorPorMesCenario1() {

        var ano = 2056; // ARRANGE

        var buscaIndicador =
                indicadorRepository.findMediaIndicadorPorMes(ano); //ACT

        assertThat(buscaIndicador.isEmpty()).isTrue(); // ASSERT
    }

    @Test
    @DisplayName("Ano Fechado - Deveria retornar dados para todos os meses, com indicadores, e verificar os 3 indicadores existentes")
    void findMediaIndicadorPorMesCenario2() {
        // Anos com dados a serem verificados
        int[] anos = {2022, 2023};

        // Verificar para cada ano
        for (int ano : anos) {
            // Act (Ação):
            List<MediaIndicadorPorMesProjection> resultado = indicadorRepository.findMediaIndicadorPorMes(ano);

            // Assert (Verificação):

            // 1. Verificar se todos os meses estão presentes e se há 3 dados para cada mês (um para cada indicador):
            assertThat(resultado).hasSize(36);

            // 2. Agrupar resultados por mês e verificar se cada grupo tem 3 elementos (indicadores)
            Map<Integer, List<MediaIndicadorPorMesProjection>> resultadosPorMes = resultado.stream()
                    .collect(Collectors.groupingBy(MediaIndicadorPorMesProjection::getMes));

            // 2. Verificar se os 3 indicadores estão presentes em todos os meses:
            List<String> origensEsperadas = List.of("distribuicao", "grerj_vinculada", "publicacao_dj"); // Adapte para suas origens

            // Verificar cada mês
            for (int mes = 1; mes <= 12; mes++) {
                final int mesFinal = mes;
                List<MediaIndicadorPorMesProjection> resultadosDoMes = resultado.stream()
                        .filter(p -> p.getMes() == mesFinal)
                        .toList();

                // Verificar se os 3 indicadores estão presentes neste mês
                assertThat(resultadosDoMes)
                        .extracting(MediaIndicadorPorMesProjection::getOrigem)
                        .containsExactlyInAnyOrderElementsOf(origensEsperadas);
            }
        }
    }


    @Test
    @DisplayName("Deveria devolver uma lista vazia quando não há dados no ano")
    void findIndicadoresPorDiaAgrupadosCenario1() {

        var ano = 2056; // ARRANGE

        var buscaIndicador =
                indicadorRepository.findIndicadoresPorDiaAgrupados(ano); //ACT

        assertThat(buscaIndicador.isEmpty()).isTrue(); // ASSERT
    }


    @Test
    @DisplayName("Verificar Indicadores por Dia - Todos os meses do ano estão presentes")
    void findIndicadoresPorDiaAgrupadosTest() {
        // Anos com dados a serem verificados
        int[] anos = {2022, 2023};

        // Verificar para cada ano
        for (int ano : anos) {
            // Act (Ação)
            List<IndicadoresDiaProjection> resultado = indicadorRepository.findIndicadoresPorDiaAgrupados(ano);

            // Assert (Verificação)

            // 1. Verificar se todos os meses estão presentes
            Set<Integer> mesesEsperados = IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toSet());

            Set<Integer> mesesEncontrados = resultado.stream()
                    .map(IndicadoresDiaProjection::getData)
                    .map(LocalDate::getMonthValue)
                    .collect(Collectors.toSet());

            assertThat(mesesEncontrados).containsExactlyInAnyOrderElementsOf(mesesEsperados);

            // 2. Verificar se os 3 indicadores estão presentes (em algum momento)
            Set<Origem.OrigemEnum> origensEncontradas = resultado.stream()
                    .map(IndicadoresDiaProjection::getOrigem)
                    .collect(Collectors.toSet());
            assertThat(origensEncontradas).containsExactlyInAnyOrder(
                    Origem.OrigemEnum.distribuicao,
                    Origem.OrigemEnum.grerj_vinculada,
                    Origem.OrigemEnum.publicacao_dj
            );




        }
    }

    @Test
    @DisplayName("Deveria devolver uma lista vazia quando não há dados no ano")
    void findByOrigemEMesEAnoCenario1() { // Arrange
    int ano= 2056; // Ano futuro onde não esperamos ter dados
    int mes = 6; // Mês válido
    Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao; // Origem válida

    // Act
    List<IndicadorHoraMesProjection> resultados = indicadorRepository.findByOrigemEMesEAno(origem, mes, ano);

    // Assert
    assertThat(resultados).isEmpty();
}
    @Test
    @DisplayName("Deveria devolver uma lista vazia quando o mês é inválido")
    void findByOrigemEMesEAnoCenario2() { // Arrange
        int ano= 2023; //Ano Correto
        int mes = 18; // Mês inválido
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao; // Origem válida

        // Act
        List<IndicadorHoraMesProjection> resultados = indicadorRepository.findByOrigemEMesEAno(origem, mes, ano);

        // Assert
        assertThat(resultados).isEmpty();
    }
    @Test
    @DisplayName("Deveria retornar os campos corretos para um ano, mês e origem válidos")
    void findByOrigemEMesEAnoCenario3() {
        // Arrange
        int ano = 2023; // Ano com dados no banco de dados
        int mes = 6;    // Mês com dados no banco de dados
        Origem.OrigemEnum origem = Origem.OrigemEnum.distribuicao; // Origem com dados no banco de dados

        // Act
        List<IndicadorHoraMesProjection> resultados = indicadorRepository.findByOrigemEMesEAno(origem, mes, ano);

        // Assert
        assertThat(resultados).isNotEmpty(); // Verifica se há resultados

        // Verifica os campos de um resultado específico (ajuste conforme seus dados)
        IndicadorHoraMesProjection resultado = resultados.get(0); // Obtém o primeiro resultado
        assertThat(resultado.getAno()).isEqualTo(ano);
        assertThat(resultado.getMes()).isEqualTo(mes);
        assertThat(resultado.getOrigem()).isEqualTo(origem);
        assertThat(resultado.getHora()).isNotNull(); // Verifica se a hora não é nula
        assertThat(resultado.getMedia()).isGreaterThan(0.0); // Verifica se a média é positiva
    }





}





