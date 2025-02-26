package com.SSproject.classes.exceptions;

import com.SSproject.classes.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Classe di risposta per errori - ora estende ResponseDTO per uniformare le risposte
    public static class ErrorResponse extends ResponseDTO<String> {
        private final String details;

        public ErrorResponse(String message, String details) {
            super(message, null);
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
}