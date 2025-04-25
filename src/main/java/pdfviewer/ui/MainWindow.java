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

        JTextArea infoArea = new JTextArea(5, 20);
        infoArea.setEditable(false);
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
