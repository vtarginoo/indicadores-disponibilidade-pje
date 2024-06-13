package br.jus.tjrj.indicadores_disponibilidade_pje.exception;

import br.jus.tjrj.indicadores_disponibilidade_pje.entity.Origem;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // Erro 400 Específico Method ArgumentTypeMismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String mensagemErro;
        if (ex.getParameter().getParameterType().isEnum()) {
            // Erro no enum (PathVariable origem)
            mensagemErro = "Origem inválida. Valores permitidos: " + Arrays.toString(Origem.OrigemEnum.values());
        } else if (ex.getRequiredType().equals(LocalDate.class)) {
            // Erro no formato da data (RequestParam data)
            mensagemErro = "Formato de data inválido. Use o formato ISO (yyyy-mm-dd).";
        } else {
            // Outros erros de tipo de argumento
            mensagemErro = "Tipo de argumento inválido para o parâmetro: " + ex.getName();
        }

        ResponseError response = new ResponseError(mensagemErro , HttpStatus.BAD_REQUEST, LocalDateTime.now());


        return ResponseEntity.badRequest().body(response);
    }

    // Erro 404 Genérico
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseError> tratarErro404(EntityNotFoundException ex) {

        ResponseError response = new ResponseError(ex.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Erro 400 Genérico
    @ExceptionHandler({ConstraintViolationException.class, ResponseStatusException.class})
    public ResponseEntity<ResponseError> tratarErro400(Exception ex) {

        if (ex instanceof ConstraintViolationException) {
            ResponseError response = new ResponseError(ExceptionUtils.formatarConstraintViolationException
                            ((ConstraintViolationException) ex, "ano", "mes"), HttpStatus.BAD_REQUEST,
                    LocalDateTime.now());
            return ResponseEntity.badRequest().body(response);
        } else if (ex instanceof ResponseStatusException) {

            ResponseStatusException rse = (ResponseStatusException) ex;
            ResponseError response = new ResponseError(rse.getReason(), HttpStatus.BAD_REQUEST,
                    LocalDateTime.now());
            return ResponseEntity.status(rse.getStatusCode()).body(response);

        } else {
            ResponseError response = new ResponseError("Erro interno no servidor", HttpStatus.BAD_REQUEST,
                    LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Erro 500 Genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> tratamentoExceptionGenerico(EntityNotFoundException ex) {

        ResponseError response = new ResponseError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }













}
