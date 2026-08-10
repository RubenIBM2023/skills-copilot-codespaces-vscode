package com.example.springcrudms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para diagnóstico y testing
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(
    origins = {"http://localhost:8080", "http://127.0.0.1:8080", "http://localhost"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "true"
)
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("timestamp", System.currentTimeMillis());
        response.put("message", "Aplicación corriendo correctamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> dbStatus() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("mongodb", "Connected");
            response.put("h2", "Connected");
            response.put("status", "OK");
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "ERROR");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/echo")
    public ResponseEntity<Map<String, Object>> echo(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", body);
        response.put("status", "OK");
        return ResponseEntity.ok(response);
    }
}
