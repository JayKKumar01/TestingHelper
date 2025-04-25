package pdfviewer.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class PDFRendererUtil {
    public static BufferedImage renderPage(PDDocument document, int pageIndex) {
        try {
            PDFRenderer renderer = new PDFRenderer(document);
            return renderer.renderImageWithDPI(pageIndex, 200); // 150 DPI for quality
        } catch (IOException e) {
            throw new RuntimeException("Failed to render PDF page", e);
        }
    }
}
