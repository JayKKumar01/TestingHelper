package pdfviewer.ui;

import pdfviewer.controller.PDFController;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainWindow extends JFrame {
    private PDFController controller;

    public MainWindow() {
        setTitle("PDF Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Choose PDF Button ---
        JButton choosePdfButton = new JButton("Choose PDF");
        choosePdfButton.addActionListener(e -> choosePdf());

        // Initial view with "Choose PDF" button
        JPanel initialPanel = new JPanel();
        initialPanel.setLayout(new BorderLayout());
        initialPanel.add(choosePdfButton, BorderLayout.CENTER);

        add(initialPanel, BorderLayout.CENTER);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void choosePdf() {
        FileDialog fileDialog = new FileDialog((Frame) null, "Select PDF", FileDialog.LOAD);
        fileDialog.setVisible(true);

        String filename = fileDialog.getFile();
        if (filename != null) {
            File selected = new File(fileDialog.getDirectory(), filename);
            String pdfPath = selected.getAbsolutePath();
            initializePdfViewer(pdfPath);
        }
    }

    private void initializePdfViewer(String pdfPath) {
        // Hide the initial "Choose PDF" screen
        getContentPane().removeAll();

        // Create a PDF controller with the selected PDF path
        controller = new PDFController(pdfPath);

        // --- Image preview label ---
        JLabel imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(250, 150));
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setVerticalAlignment(JLabel.CENTER);

        // --- Word details area (no scroll pane) ---
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setPreferredSize(new Dimension(250, 300));
        infoArea.setMaximumSize(new Dimension(250, 300));  // Limit the max size

        // --- East panel to hold both components ---
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new GridBagLayout());  // Use GridBagLayout for flexibility
        GridBagConstraints gbc = new GridBagConstraints();
        eastPanel.setPreferredSize(new Dimension(250, 0));  // Initial preferred size

        // Set padding for the east panel
        eastPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Top, Left, Bottom, Right padding

        // Add components to the east panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;  // Center both components in the panel
        eastPanel.add(imagePreview, gbc);

        gbc.gridy = 1;
        eastPanel.add(Box.createRigidArea(new Dimension(0, 10)), gbc); // Optional spacing

        gbc.gridy = 2;
        eastPanel.add(infoArea, gbc);

        // --- Go Back Button ---
        JButton goBackButton = new JButton("Go Back");
        goBackButton.addActionListener(e -> goBackToFileChooser());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(goBackButton);

        // PDF panel and navigation panel
        PDFPanel pdfPanel = new PDFPanel(controller, infoArea, imagePreview);
        NavigationPanel navPanel = new NavigationPanel(controller, pdfPanel);

        // Add panels to the main frame
        add(pdfPanel, BorderLayout.CENTER);
        add(navPanel, BorderLayout.SOUTH);
        add(eastPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.NORTH);

        // Revalidate and repaint to show the PDF viewer
        revalidate();
        repaint();
    }

    private void goBackToFileChooser() {
        // Remove the current layout and show the file chooser again
        getContentPane().removeAll();
        SwingUtilities.invokeLater(() -> new MainWindow());
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        // Start the application
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
