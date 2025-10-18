import javax.swing.*;

public class UI extends JFrame {

    public UI() {
        setTitle("Crypto Loacker");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UI ui = new UI();
            ui.setVisible(true);
        });
    }
}
