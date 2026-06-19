package com.ejemplo.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    // Se creará una carpeta "uploads" en la raíz del proyecto para guardar los archivos
    private final String UPLOAD_DIR = "uploads/";

    @PostMapping("/subir")
    public ResponseEntity<String> subirArchivo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: El archivo está vacío.");
        }

        try {
            // 1. Asegurar que el directorio exista
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 2. Ruta final donde se guardará el archivo
            Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            
            // 3. Guardar el archivo en disco (sobrescribe si ya existe uno con el mismo nombre)
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("¡Archivo '" + file.getOriginalFilename() + "' subido exitosamente a la carpeta 'uploads'!");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al subir el archivo: " + e.getMessage());
        }
    }
}
