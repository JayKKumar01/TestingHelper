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

    public PDFPanel(PDFController controller, JEditorPane infoArea) {
        this.controller = controller;
        updateContent();


        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentWords == null){
                    return;
                }
                Point cursor = e.getPoint();

                double scale = getScale();
                int offsetX = (getWidth() - (int) (currentImage.getWidth() * scale)) / 2;
                int offsetY = (getHeight() - (int) (currentImage.getHeight() * scale)) / 2;

                // Convert screen point to image coordinates
                int imgX = (int) ((cursor.x - offsetX) / scale);
                int imgY = (int) ((cursor.y - offsetY) / scale);

                for (WordInfo word : currentWords) {
                    Rectangle box = word.getBoundingBox();
                    if (box != null && box.contains(imgX, imgY)) {
                        Font wordFont = word.getAWTFont();
                        Font normalFont = new Font("Serif", Font.PLAIN, 14);

                        String details = word.getDetails();
                        String[] lines = details.split("\n");

                        // Use HTML to support font changes
                        StringBuilder html = new StringBuilder("<html>");
                        html.append("<span style='font-family:")
                                .append(wordFont.getFamily())
                                .append("; font-size:")
                                .append(wordFont.getSize())
                                .append("pt;'>")
                                .append(lines[0])
                                .append("</span><br>");

                        for (int i = 1; i < lines.length; i++) {
                            html.append("<span style='font-family:")
                                    .append(normalFont.getFamily())
                                    .append("; font-size:")
                                    .append(normalFont.getSize())
                                    .append("pt;'>")
                                    .append(lines[i])
                                    .append("</span><br>");
                        }
                        html.append("</html>");
                        infoArea.setContentType("text/html");
                        infoArea.setText(html.toString());

                        infoArea.setText(word.getDetails());
                        return;
                    }
                }
                infoArea.setText(""); // Clear if not on a word
            }
        });
    }

    public void updateContent() {
        currentImage = controller.renderCurrentPage();
        currentWords = controller.getWordInfoForCurrentPage(); // You must implement this in the controller
        System.out.println(currentWords.size());
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
