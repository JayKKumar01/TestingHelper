package pdfviewer.controller;

import pdfviewer.models.AppState;
import pdfviewer.models.WordInfo;
import pdfviewer.utils.PDFRendererUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import pdfviewer.utils.PdfWordExtractor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFController {
    private final AppState state;
    private final PDDocument document;

    public PDFController(String pdfPath) {
        try {
            this.document = PDDocument.load(new File(pdfPath));
            this.state = new AppState(0, document.getNumberOfPages());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load PDF", e);
        }
    }

    public BufferedImage renderCurrentPage() {
        return PDFRendererUtil.renderPage(document, state.getCurrentPage());
    }

    public void nextPage() {
        if (state.getCurrentPage() < state.getTotalPages() - 1) {
            state.setCurrentPage(state.getCurrentPage() + 1);
        }
    }

    public void prevPage() {
        if (state.getCurrentPage() > 0) {
            state.setCurrentPage(state.getCurrentPage() - 1);
        }
    }

    public String getPageLabel() {
        return "Page " + (state.getCurrentPage() + 1) + " of " + state.getTotalPages();
    }

    public List<WordInfo> getWordInfoForCurrentPage(){
        try {
            return PdfWordExtractor.extractWords(document,state.getCurrentPage()+1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
