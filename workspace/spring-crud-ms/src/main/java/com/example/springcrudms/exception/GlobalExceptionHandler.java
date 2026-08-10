package com.example.springcrudms.exception;

import com.example.springcrudms.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleProductNotFound(
            ProductNotFoundException ex, HttpServletRequest request) {
        return new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> violations = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            violations.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            violations,
            request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleGeneric(Exception ex, HttpServletRequest request) {
        // Log el error completo
        System.err.println("====== ERROR EN ENDPOINT ======");
        System.err.println("URL: " + request.getRequestURI());
        System.err.println("Método: " + request.getMethod());
        System.err.println("Error: " + ex.getClass().getSimpleName());
        System.err.println("Mensaje: " + ex.getMessage());
        System.err.println("Stack Trace:");
        ex.printStackTrace();
        System.err.println("==============================");
        
        return new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getClass().getSimpleName() + ": " + ex.getMessage(),
            request.getRequestURI()
        );
    }
}
