import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * UI class per l'interfaccia grafica.
 * Fornisce selezione directory, scelta algoritmo, inserimento chiave/IV e pulsanti ENCRYPT/DECRYPT.
 */
public class UI extends JFrame {
    // Panel principali
    private JPanel panel, buttonPanel, inputPanel;

    // Pulsanti principali
    private JButton encryptButton, decryptButton;
    private JButton fileDialogButton;

    // Etichette e campi input
    private JLabel algorithmLabel, keyLabel, ivLabel;
    private JComboBox<String> algorithmComboBox;
    private JTextField keyTextField, ivTextField, directoryTextField;

    // Directory scelta dall'utente (oggetto File)
    private File choosenDirectory;

    // Costruttore: costruisce e dispone tutti i componenti UI
    public UI() {
        setTitle("Crypto Loacker");
        // Imposta comportamento di chiusura finestra
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Dimensione e posizione iniziale della finestra
        setSize(700, 420);
        setLocationRelativeTo(null);

        // Panel principale con bordo e layout BorderLayout
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        add(panel);

        // Panel centrale per gli input con GridBagLayout per un posizionamento flessibile
        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Options"));
        panel.add(inputPanel, BorderLayout.CENTER);

        // Panel in basso per i pulsanti ENCRYPT/DECRYPT
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Font usati per etichette e campi
        Font labelFont = new Font("SansSerif", Font.BOLD, 12);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 12);

        // Pulsante per aprire il JFileChooser per selezionare una directory
        fileDialogButton = new JButton("Scegli directory");
        // Icona standard per le directory, se disponibile
        Icon dirIcon = UIManager.getIcon("FileView.directoryIcon");
        if (dirIcon != null) fileDialogButton.setIcon(dirIcon);
        // Mnemonic e tooltip per accessibilità
        fileDialogButton.setMnemonic(KeyEvent.VK_D);
        fileDialogButton.setToolTipText("Apri il selettore di directory");
        fileDialogButton.setFont(fieldFont);

        // Azione del pulsante: apre JFileChooser in modalità directory-only
        fileDialogButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Select Directory");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                // Salva la directory scelta e mostra il percorso nel campo non editabile
                choosenDirectory = chooser.getSelectedFile();
                directoryTextField.setText(choosenDirectory.getAbsolutePath());
            }
        });

        // Campo che mostra il percorso della directory selezionata (non editabile)
        directoryTextField = new JTextField(30);
        directoryTextField.setEditable(false);
        directoryTextField.setFont(fieldFont);
        directoryTextField.setToolTipText("Percorso della directory selezionata");

        // Configurazione GridBagConstraints comune per posizionamento
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Posiziona il pulsante di scelta directory (colonna 0, riga 0)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(fileDialogButton, gbc);

        // Posiziona il campo percorso accanto al pulsante (colonna 1, riga 0)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        inputPanel.add(directoryTextField, gbc);

        // Label e combo per la scelta dell'algoritmo
        algorithmLabel = new JLabel("Select algorithm:");
        algorithmLabel.setFont(labelFont);
        algorithmComboBox = new JComboBox<>(new String[]{"DES-ECB", "DES-CBC", "AES-256-ECB", "AES-256-CBC"});
        algorithmComboBox.setFont(fieldFont);
        algorithmComboBox.setToolTipText("Scegli l'algoritmo di cifratura");

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(algorithmLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        inputPanel.add(algorithmComboBox, gbc);

        // Label e campo per la chiave
        keyLabel = new JLabel("Enter the key:");
        keyLabel.setFont(labelFont);
        keyTextField = new JTextField(25);
        keyTextField.setFont(fieldFont);
        keyTextField.setToolTipText("Inserisci la chiave");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        inputPanel.add(keyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        inputPanel.add(keyTextField, gbc);

        // Label e campo per l'IV (vettore di inizializzazione)
        ivLabel = new JLabel("Enter the IV:");
        ivLabel.setFont(labelFont);
        ivTextField = new JTextField(25);
        ivTextField.setFont(fieldFont);
        ivTextField.setToolTipText("Inserisci il vettore di inizializzazione (IV) se necessario");

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        inputPanel.add(ivLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        inputPanel.add(ivTextField, gbc);

        // Pulsanti ENCRYPT e DECRYPT con tooltip e font
        encryptButton = new JButton("ENCRYPT");
        decryptButton = new JButton("DECRYPT");
        encryptButton.setFont(fieldFont);
        decryptButton.setFont(fieldFont);
        encryptButton.setToolTipText("Avvia la cifratura");
        decryptButton.setToolTipText("Avvia la decifratura");

        // Azione ENCRYPT: crea istanza di Loacker con i parametri correnti
        encryptButton.addActionListener(e -> {
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            String key = keyTextField.getText();
            String iv = ivTextField.getText();
            Loacker loacker = new Loacker(choosenDirectory, algorithm, key, iv);
            //loacker.encrypt();
        });

        // Azione DECRYPT: crea istanza di Loacker con i parametri correnti
        decryptButton.addActionListener(e -> {
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            String key = keyTextField.getText();
            String iv = ivTextField.getText();
            Loacker loacker = new Loacker(choosenDirectory, algorithm, key, iv);
            //loacker.decrypt();
        });

        // Aggiunge i pulsanti al pannello inferiore
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UI ui = new UI();
            ui.setVisible(true);
        });
    }
}
