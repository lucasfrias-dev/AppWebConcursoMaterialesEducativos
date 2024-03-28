package ar.edu.unnoba.appweb_concurso_materiales_educativos.service.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class PdfService {

    /**
     * Convierte un archivo PDF en una imagen PNG.
     * @param pdfFilePath La ruta del archivo PDF que se va a convertir.
     * @return El archivo de imagen PNG generado.
     * @throws IOException Si ocurre un error durante la carga o escritura del archivo.
     */
    public File convertPdfToImage(String pdfFilePath) throws IOException {
        // Crea un objeto File a partir de la ruta del archivo PDF
        File pdfFile = new File(pdfFilePath);

        // Carga el documento PDF usando la biblioteca PDFBox
        PDDocument document = PDDocument.load(pdfFile);

        // Crea un renderizador para renderizar el PDF como una imagen
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        // Renderiza la primera página del PDF como una imagen en formato BufferedImage
        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300);

        // Define la ruta y el nombre de archivo para la imagen PNG de salida
        File outputfile = new File("src/main/resources/static/images/" + pdfFile.getName() + ".png");

        // Escribe la imagen BufferedImage en el archivo de salida como formato PNG
        ImageIO.write(bim, "png", outputfile);

        // Cierra el documento PDF
        document.close();

        // Devuelve el archivo de imagen PNG generado
        return outputfile;
    }

}
