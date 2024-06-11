package br.jus.tjrj.indicadores_disponibilidade_pje.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> tratarErro404(EntityNotFoundException ex) {
        String mensagemErro = ex.getMessage(); // Obtém a mensagem da exceção
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensagemErro); // Retorna a mensagem no corpo da resposta
    }






}
