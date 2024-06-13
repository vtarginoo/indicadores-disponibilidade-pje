package br.jus.tjrj.indicadores_disponibilidade_pje.exception;

import jakarta.validation.ConstraintViolationException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ExceptionUtils {

    public static String formatarConstraintViolationException(ConstraintViolationException ex, String... nomesParametros) {
        Set<String> parametrosSet = new HashSet<>(Arrays.asList(nomesParametros));

        return ex.getConstraintViolations().stream()
                .map(violation -> {
                    String propriedade = violation.getPropertyPath().toString();
                    String[] partesPropriedade = propriedade.split("\\."); // Divide o caminho em partes
                    String nomeParametro = partesPropriedade[partesPropriedade.length - 1]; // Pega a última parte

                    String mensagem = violation.getMessage();
                    if (parametrosSet.contains(nomeParametro)) {
                        mensagem = String.format("O parâmetro '%s' %s. Valor informado: %s",
                                nomeParametro, mensagem, violation.getInvalidValue());
                    }
                    return mensagem;
                })
                .collect(Collectors.joining(", "));
    }
}

