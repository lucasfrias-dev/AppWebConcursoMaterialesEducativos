package ar.edu.unnoba.appweb_concurso_materiales_educativos.controller.advice;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.exception.FileStorageException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FileStorageException.class)
    public String handleFileStorageException(FileStorageException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/concursante/postular-material";
    }
}
