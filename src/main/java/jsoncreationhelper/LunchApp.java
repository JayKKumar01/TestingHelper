package jsoncreationhelper;

import jsoncreationhelper.models.InputData;
import jsoncreationhelper.utils.InputDataProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LunchApp {

    public static void setUseCommon(boolean useCommon) {
        Config.shouldUseCommon = useCommon;
        System.out.println("🔁 Use Common set to: " + useCommon);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JSON Creation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 450);
            frame.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Label
            JLabel label = new JLabel("Enter Form ID:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            frame.add(label, gbc);

            // Text Field
            JTextField formIdField = new JTextField(20);
            gbc.gridx = 1;
            gbc.gridy = 0;
            frame.add(formIdField, gbc);

            // Create Button
            JButton createButton = new JButton("Create");
            gbc.gridx = 2;
            gbc.gridy = 0;
            frame.add(createButton, gbc);

            // Use Common Checkbox
            JCheckBox useCommonCheckbox = new JCheckBox("Use Common");
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 3;
            gbc.anchor = GridBagConstraints.WEST;
            frame.add(useCommonCheckbox, gbc);

            useCommonCheckbox.addActionListener(e -> setUseCommon(useCommonCheckbox.isSelected()));

            // Log Area
            JTextArea logArea = new JTextArea(15, 50);
            logArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(logArea);
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 3;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            frame.add(scrollPane, gbc);

            // Redirect System.out to logArea
            PrintStream printStream = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {
                    logArea.append(String.valueOf((char) b));
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                }

                @Override
                public void write(byte[] b, int off, int len) {
                    logArea.append(new String(b, off, len));
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                }
            });
            System.setOut(printStream);
            System.setErr(printStream);


            // Center and show
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Create Button Action
            createButton.addActionListener((ActionEvent e) -> {
                String formId = formIdField.getText().trim();
                if (formId.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a Form ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<InputData> models = InputDataProvider.load(formId);
                        if (models == null || models.isEmpty()) {
                            System.out.println("❌ No input data found for Form ID: " + formId);
                            JOptionPane.showMessageDialog(frame, "No input data found for Form ID: " + formId, "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        System.out.println("🚀 Starting JSON creation for Form ID: " + formId);
                        JsonCreator.createJson(formId, models);
                    }
                });


            });
        });
    }
}
