package ar.edu.unnoba.appweb_concurso_materiales_educativos.service.file;

import ar.edu.unnoba.appweb_concurso_materiales_educativos.exception.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    public String saveFile(MultipartFile file){
        try {
            // Verifica si el archivo no está vacío
            if (!file.isEmpty()) {
                // Genera un nombre único para el archivo
                String originalFilename = file.getOriginalFilename();
                String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

                // Define el directorio de subida
                String uploadsDir = "src/main/resources/static/file/";
                Path uploadPath = Paths.get(uploadsDir);
                // Si el directorio no existe, lo crea
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                // Define el path del archivo
                Path filePath = uploadPath.resolve(uniqueFilename);
                // Copia el archivo al directorio de subida
                Files.copy(file.getInputStream(), filePath);

                // Devuelve el path del archivo
                return uploadsDir + uniqueFilename;
            }
            return null;
        } catch (IOException e) {
            throw new FileStorageException("No se pudo almacenar el archivo " + file.getOriginalFilename() + ". ¡Inténtalo de nuevo!", e);
        }

    }
}
