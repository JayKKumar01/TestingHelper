package pdfviewer.ui;

import pdfviewer.controller.PDFController;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public MainWindow(String pdfPath) {
        setTitle("PDF Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        PDFController controller = new PDFController(pdfPath);

        JEditorPane infoArea = new JEditorPane();
        infoArea.setEditable(false);
        infoArea.setContentType("text/html");
        infoArea.setPreferredSize(new Dimension(250, 0)); // Set width for the pane

        JScrollPane infoScrollPane = new JScrollPane(infoArea);

        PDFPanel pdfPanel = new PDFPanel(controller, infoArea);
        NavigationPanel navPanel = new NavigationPanel(controller, pdfPanel);

        add(pdfPanel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);
        add(infoScrollPane, BorderLayout.EAST);

        setSize(800, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
