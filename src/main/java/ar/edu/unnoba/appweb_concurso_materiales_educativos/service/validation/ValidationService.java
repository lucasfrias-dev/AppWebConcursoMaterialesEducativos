package ar.edu.unnoba.appweb_concurso_materiales_educativos.service.validation;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.model.Concurso;
import ar.edu.unnoba.appweb_concurso_materiales_educativos.service.ConcursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ValidationService {

    // Inyecta el servicio de concurso
    @Autowired
    private ConcursoService concursoService;

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

        // Verifica si hay un concurso vigente.
        Concurso concurso = concursoService.getConcursoActual();
        if (concurso == null) {
            return "No puedes postular ningún material porque no hay un concurso vigente.";
        }

        // Si no hay errores, devuelve null
        return null;
    }
}
