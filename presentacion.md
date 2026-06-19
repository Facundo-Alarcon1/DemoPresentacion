# Presentación: Tareas Programadas y Carga de Archivos



## Tema 1: Tareas Programadas (Scheduled Tasks)

### 1. Introducción completa del tema
Las **Tareas Programadas** (o *Scheduled Tasks* / *Cron Jobs*) son rutinas o procesos informáticos que se configuran para ejecutarse automáticamente en momentos específicos, después de ciertos intervalos de tiempo, o de forma periódica. En lugar de que un usuario o una petición externa active el código, el propio sistema se encarga de "despertar" y ejecutar la tarea según un calendario predefinido.

### 2. Para qué sirve y qué problema resuelve
**¿Para qué sirve?**
Sirve para automatizar operaciones de mantenimiento, procesamiento por lotes (batch processing) o sincronización de datos que no requieren intervención humana.

**¿Qué problema resuelve?**
Resuelve la necesidad de ejecutar procesos en segundo plano sin bloquear la aplicación principal y sin depender de disparadores manuales. Ejemplos de problemas que soluciona:
- **Limpieza de datos:** Borrar registros temporales o carritos de compra abandonados cada medianoche.
- **Notificaciones:** Enviar correos electrónicos de resumen semanal a los usuarios todos los lunes a las 8 AM.
- **Reportes:** Generar facturas o reportes financieros al final de cada mes.
- **Sincronización:** Consultar una API externa cada 10 minutos para actualizar el tipo de cambio de divisas.

### 3. Cómo se utiliza dentro de una aplicación
En Spring Boot, implementar tareas programadas es extremadamente sencillo gracias a las anotaciones:
1.  **Habilitar el soporte:** Se agrega la anotación `@EnableScheduling` en la clase principal de la aplicación o en una clase de configuración.
2.  **Definir la tarea:** Se crea un método dentro de un componente de Spring (`@Component`, `@Service`) y se le agrega la anotación `@Scheduled`.
3.  **Configurar el disparador:** En el `@Scheduled` se define cuándo se ejecuta usando:
    *   `fixedRate`: Se ejecuta cada X milisegundos (ej. cada 5 segundos).
    *   `fixedDelay`: Se ejecuta X milisegundos después de que termine la ejecución anterior.
    *   `cron`: Expresión similar a los sistemas Unix que permite fechas específicas (ej. "0 0 12 * * ?" para todos los días a las 12:00 PM).

### 4. Ejemplo práctico de implementación
```java
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

// 1. En la clase principal (o de configuración) iría @EnableScheduling
// @SpringBootApplication
// @EnableScheduling
// public class MiAplicacion { ... }

@Component
public class TareasAutomatizadas {

    // Se ejecuta cada 10 segundos (10000 milisegundos)
    @Scheduled(fixedRate = 10000)
    public void reportarHoraActual() {
        System.out.println("Tarea programada ejecutada. Hora actual: " + LocalDateTime.now());
    }

    // Se ejecuta todos los días a las 8:00 AM usando una expresión Cron
    @Scheduled(cron = "0 0 8 * * *")
    public void enviarCorreosDiarios() {
        System.out.println("Enviando correos de resumen a los usuarios...");
        // Lógica para consultar base de datos y enviar emails
    }
}
```

---

## Tema 2: Cargar fotos de perfil y archivos (File Upload)

### 1. Introducción completa del tema
La **Carga de Archivos** es el proceso mediante el cual un cliente (un navegador web, una aplicación móvil) transfiere un archivo local (imágenes, documentos PDF, videos, etc.) hacia el servidor. En la web, esto se realiza generalmente a través de peticiones HTTP utilizando el formato `multipart/form-data`, el cual permite enviar texto y datos binarios en la misma petición.

### 2. Para qué sirve y qué problema resuelve
**¿Para qué sirve?**
Sirve para recibir contenido generado por el usuario y almacenarlo de forma segura, ya sea en el disco duro del servidor, en una base de datos o en servicios de almacenamiento en la nube (como Amazon S3, Google Cloud Storage).

**¿Qué problema resuelve?**
Resuelve la necesidad de personalizar perfiles (fotos de usuario), adjuntar evidencia o documentación a registros (ej. subir un CV, comprobante de pago), y compartir medios. El principal reto que soluciona el framework es parsear (desmenuzar) la petición binaria que llega por HTTP y convertirla en un objeto manejable dentro del código.

### 3. Cómo se utiliza dentro de una aplicación
En una API REST con Spring Boot:
1.  **Recepción en el Controlador:** Se crea un endpoint (ej. `@PostMapping`) que espera un parámetro de tipo `MultipartFile`.
2.  **Procesamiento:** Se extrae el nombre original del archivo y su contenido (bytes).
3.  **Almacenamiento:** Se guarda el archivo utilizando las clases de entrada/salida de Java (ej. `Files.copy()`) en una ruta específica del servidor.
4.  **Configuración (Opcional):** En el archivo `application.properties` se deben configurar los límites de tamaño para evitar que los usuarios suban archivos gigantes que saturen la memoria:
    ```properties
    spring.servlet.multipart.max-file-size=5MB
    spring.servlet.multipart.max-request-size=10MB
    ```

### 4. Ejemplo práctico de implementación
```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    // Directorio donde se guardarán las fotos (debe existir o ser creado)
    private final String DIRECTORIO_SUBIDAS = "uploads/perfiles/";

    @PostMapping("/subir-foto")
    public ResponseEntity<String> subirFotoPerfil(@RequestParam("file") MultipartFile archivo, 
                                                  @RequestParam("usuarioId") Long usuarioId) {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        try {
            // 1. Obtener nombre y crear la ruta de destino
            String nombreArchivo = usuarioId + "_" + archivo.getOriginalFilename();
            Path rutaDestino = Paths.get(DIRECTORIO_SUBIDAS + nombreArchivo);

            // 2. Crear el directorio si no existe
            Files.createDirectories(rutaDestino.getParent());

            // 3. Guardar el archivo en el servidor (sobrescribe si ya existe)
            Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            // 4. Aquí iría la lógica para guardar 'rutaDestino' en la base de datos para este usuario

            return ResponseEntity.ok("Foto subida exitosamente: " + nombreArchivo);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al guardar el archivo");
        }
    }
}
```

---
