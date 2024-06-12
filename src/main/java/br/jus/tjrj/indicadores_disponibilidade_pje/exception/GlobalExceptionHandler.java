package br.jus.tjrj.indicadores_disponibilidade_pje.exception;

import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarErro404(EntityNotFoundException ex) {
        String mensagemErro = ex.getMessage(); // Obtém a mensagem da exceção
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensagemErro); // Retorna a mensagem no corpo da resposta
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    public ResponseEntity<String> tratarErro400(Exception ex) {

        String errorMessage = "Valor inválido para o parâmetro 'origem'. Valores permitidos: "
                + Arrays.stream(Origem.OrigemEnum.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        if (ex instanceof MethodArgumentTypeMismatchException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        } else if (ex instanceof ConstraintViolationException) {
            return ResponseEntity.badRequest().body(formatarConstraintViolationException((ConstraintViolationException) ex));
        } else {
            // Retorno padrão para exceções não tratadas explicitamente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor");
        }
    }

    // Método auxiliar para formatar a mensagem de ConstraintViolationException
    private String formatarConstraintViolationException(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }




}
