package com.SSproject.classes.exceptions;

import com.SSproject.classes.response.ResponseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@ControllerAdvice(annotations = RestController.class) // Applica solo ai controller REST
public class RestExceptionHandler {

    // Classe di risposta per errori - ora estende ResponseDTO per uniformare le risposte
    public static class ErrorResponse extends ResponseDTO<String> {
        private final String details;

        public ErrorResponse(String message, String details) {
            super("ERROR", message, null);
            this.details = details;
        }

        public String getDetails() {
            return details;
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGenericException(Exception e) {
        return new ErrorResponse("Errore interno del server", e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleNullPointerException(NullPointerException e) {
        return new ErrorResponse("Si è verificato un errore di riferimento nullo", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return new ErrorResponse("Parametri non validi", e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = e.getMessage() != null && e.getMessage().contains("username")
                ? "Username già esistente"
                : "Violazione dei vincoli di integrità dei dati";
        return new ErrorResponse(message, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ErrorResponse("Dati utente non validi", details);
    }
}