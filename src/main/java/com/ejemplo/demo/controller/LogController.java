package com.ejemplo.demo.controller;

import com.ejemplo.demo.tasks.MisTareasProgramadas;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final MisTareasProgramadas tareas;

    public LogController(MisTareasProgramadas tareas) {
        this.tareas = tareas;
    }

    // Retorna los últimos logs generados por las tareas programadas
    @GetMapping
    public List<String> getLogs() {
        return tareas.getLogs();
    }
}
