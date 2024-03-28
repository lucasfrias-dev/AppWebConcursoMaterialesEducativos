package ar.edu.unnoba.appweb_concurso_materiales_educativos.service.validation;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.ConcursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ValidationService {

    // Inyecta el servicio de concurso
    private final ConcursoService concursoService;

    @Autowired
    public ValidationService(ConcursoService concursoService) {
        this.concursoService = concursoService;
    }

    public String validateCreateMaterial(BindingResult result, MultipartFile file) {
        // Verifica si hay errores de validación.
        if (result.hasErrors()) {
            // Devuelve un mensaje de error
            return "Hay errores de validación en el formulario.";
        }

        // Verifica si el archivo está presente.
        if (file.isEmpty()) {
            return "El archivo es obligatorio.";
        }

        // Verifica si el archivo excede el tamaño máximo (15MB en este caso).
        if (!file.isEmpty() && file.getSize() > 15 * 1024 * 1024) {
            return "El archivo excede el tamaño máximo permitido (15MB).";
        }

        // Verifica que el archivo es un PDF
        if (!Objects.requireNonNull(file.getContentType()).equalsIgnoreCase("application/pdf")) {
            return "El archivo debe ser un PDF";
        }

        // No se puede postular si no se encuentra en fechas de postulacion
        Concurso concursoActual = concursoService.getConcursoActual();
        if (concursoActual == null) {
            return "No hay concurso en curso.";
        }
        LocalDateTime fechaActual = LocalDateTime.now();
        boolean postulacionAbierta = fechaActual.isAfter(concursoActual.getFechaInicio()) && fechaActual.isBefore(concursoActual.getFechaFinPostulaciones());
        if (!postulacionAbierta) {
            return "No se puede postular en este momento debido a que no se encuentra en fechas de postulación. Consulte las fechas del concurso actual.";
        }

        // Si no hay errores, devuelve null
        return null;
    }
}
