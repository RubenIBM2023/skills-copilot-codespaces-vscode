package com.example.springcrudms.dto;

import java.time.Instant;

public class ErrorResponseDTO {
    private int status;
    private String error;
    private Object message;
    private String path;
    private Instant timestamp;

    public ErrorResponseDTO(int status, String error, Object message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now();
    }

    // Getters de todos los campos
    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Object getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
