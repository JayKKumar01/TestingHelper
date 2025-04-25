package pdfviewer.main;

import pdfviewer.ui.MainWindow;

public class AppLauncher {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainWindow("C:\\Users\\jayte\\Downloads\\lekl101.pdf"); // Change path accordingly
        });
    }
}
