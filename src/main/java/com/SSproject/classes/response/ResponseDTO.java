package com.SSproject.classes.response;

import java.time.LocalDateTime;

public class ResponseDTO<T> {
    private final LocalDateTime timestamp;
    private final String status;
    private final String message;
    private final T data;

    // Costruttore per risposte di successo con dati
    public ResponseDTO(String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = "SUCCESS";
        this.message = message;
        this.data = data;
    }

    // Costruttore per risposte di successo senza dati
    public ResponseDTO(String message) {
        this.timestamp = LocalDateTime.now();
        this.status = "SUCCESS";
        this.message = message;
        this.data = null;
    }

    // Costruttore per risposte di errore
    public ResponseDTO(String status, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getter e setter
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}