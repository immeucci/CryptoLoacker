import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UI extends JFrame {
    private JPanel panel, buttonPanel, inputPanel;
    private JButton encryptButton, decryptButton;
    private JButton fileDialogButton;
    private JLabel algorithmLabel, keyLabel, ivLabel;
    private JComboBox<String> algorithmComboBox;
    private JTextField keyTextField, ivTextField, directoryTextField;

    public UI() {
        setTitle("Crypto Loacker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(400, 200);

        panel = new JPanel(new GridLayout(2, 1));
        add(panel);

        inputPanel = new JPanel(new GridLayout(4, 2));
        add(inputPanel);

        buttonPanel = new JPanel(new FlowLayout());
        add(buttonPanel);

        fileDialogButton = new JButton("CHOOSE DIRECTORY");
        directoryTextField = new JTextField();
        directoryTextField.setEditable(false);

        inputPanel.add(fileDialogButton);

        algorithmLabel = new JLabel("Select algorithm:");
        algorithmComboBox = new JComboBox<>(new String[]{"DES-ECB", "DES-CBC", "AES-256-ECB", "AES-256-CBC"});

        keyLabel = new JLabel("Enter the key:");
        keyTextField = new JTextField(20);

        ivLabel = new JLabel("Enter the IV:");
        ivTextField = new JTextField(20);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UI ui = new UI();
            ui.setVisible(true);
        });
    }
}
