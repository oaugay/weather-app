import javax.swing.*;

public class Applauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
                  new WeatherAppGui().setVisible(true);
        });
    }
}
