package pdfviewer.ui;

import pdfviewer.controller.PDFController;
import pdfviewer.models.WordInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class PDFPanel extends JPanel {
    private final PDFController controller;
    private BufferedImage currentImage;
    private List<WordInfo> currentWords;

    public PDFPanel(PDFController controller, JTextArea infoArea, JLabel imagePreview) {
        this.controller = controller;
        updateContent();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentWords == null || currentImage == null) return;

                Point cursor = e.getPoint();
                double scale = getScale();
                int offsetX = (getWidth() - (int) (currentImage.getWidth() * scale)) / 2;
                int offsetY = (getHeight() - (int) (currentImage.getHeight() * scale)) / 2;

                int imgX = (int) ((cursor.x - offsetX) / scale);
                int imgY = (int) ((cursor.y - offsetY) / scale);

                for (WordInfo word : currentWords) {
                    Rectangle box = word.getBoundingBox();
                    if (box != null && box.contains(imgX, imgY)) {
                        infoArea.setText(word.getDetails());

                        // Crop word image
                        BufferedImage wordImage = currentImage.getSubimage(
                                Math.max(0, box.x),
                                Math.max(0, box.y),
                                Math.min(box.width, currentImage.getWidth() - box.x),
                                Math.min(box.height, currentImage.getHeight() - box.y)
                        );
                        imagePreview.setIcon(new ImageIcon(wordImage));
                        return;
                    }
                }

                infoArea.setText("");
                imagePreview.setIcon(null);
            }
        });
    }


    public void updateContent() {
        currentImage = controller.renderCurrentPage();
        currentWords = controller.getWordInfoForCurrentPage(); // You must implement this in the controller
        repaint();
    }

    private double getScale() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int imgWidth = currentImage.getWidth();
        int imgHeight = currentImage.getHeight();
        return Math.min((double) panelWidth / imgWidth, (double) panelHeight / imgHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentImage != null) {
            double scale = getScale();
            int drawWidth = (int) (currentImage.getWidth() * scale);
            int drawHeight = (int) (currentImage.getHeight() * scale);
            int x = (getWidth() - drawWidth) / 2;
            int y = (getHeight() - drawHeight) / 2;
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(currentImage, x, y, drawWidth, drawHeight, this);
        }
    }
}
