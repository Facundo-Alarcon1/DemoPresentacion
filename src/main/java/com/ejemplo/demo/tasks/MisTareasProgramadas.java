package com.ejemplo.demo.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class MisTareasProgramadas {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final List<String> logs = new ArrayList<>();

    // Ejecuta la tarea cada 5 segundos
    @Scheduled(fixedRate = 5000)
    public void tareaFrecuente() {
        String msg = "[TAREA PROGRAMADA] Verificando sistema... Hora: " + LocalDateTime.now().format(formatter);
        System.out.println(msg);
        addLog(msg);
    }

    // Esta simula algo más pesado, ejecutándose cada 15 segundos
    @Scheduled(fixedRate = 15000)
    public void limpiezaDeArchivos() {
        String msg = "[TAREA PROGRAMADA] Ejecutando limpieza de archivos temporales... Hora: " + LocalDateTime.now().format(formatter);
        System.out.println(msg);
        addLog(msg);
    }

    private synchronized void addLog(String message) {
        logs.add(0, message); // Agregar al inicio (más reciente primero)
        if (logs.size() > 10) {
            logs.remove(10); // Mantener solo los últimos 10 logs
        }
    }

    public synchronized List<String> getLogs() {
        return new ArrayList<>(logs);
    }
}
