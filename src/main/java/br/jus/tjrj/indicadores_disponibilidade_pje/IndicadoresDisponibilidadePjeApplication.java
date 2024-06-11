package br.jus.tjrj.indicadores_disponibilidade_pje;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Indicadores de Disponibilidade PJe", // Novo título
				version = "1.0.0",
				description = "API para consulta de indicadores de disponibilidade do PJe"
		)
)
public class IndicadoresDisponibilidadePjeApplication {

	public static void main(String[] args) {
		SpringApplication.run(IndicadoresDisponibilidadePjeApplication.class, args);
	}

}
