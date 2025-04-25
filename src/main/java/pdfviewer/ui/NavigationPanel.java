package pdfviewer.ui;

import pdfviewer.controller.PDFController;

import javax.swing.*;
import java.awt.*;

public class NavigationPanel extends JPanel {
    public NavigationPanel(PDFController controller, PDFPanel pdfPanel) {
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JLabel pageLabel = new JLabel(controller.getPageLabel());

        prevButton.addActionListener(e -> {
            controller.prevPage();
            pageLabel.setText(controller.getPageLabel());
            pdfPanel.updateContent();
        });

        nextButton.addActionListener(e -> {
            controller.nextPage();
            pageLabel.setText(controller.getPageLabel());
            pdfPanel.updateContent();
        });

        add(prevButton);
        add(pageLabel);
        add(nextButton);
    }
}
