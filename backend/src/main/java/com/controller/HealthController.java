package com.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import javax.sql.DataSource;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
        try (var connection = dataSource.getConnection()) {
            String database = connection.getMetaData().getDatabaseProductName();
            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "application", "Campus Connect Backend API",
                    "database", database
            ));
        } catch (SQLException exception) {
            return ResponseEntity.status(503).body(Map.of(
                    "status", "DOWN",
                    "application", "Campus Connect Backend API",
                    "database", "unavailable"
            ));
        }
    }
}
