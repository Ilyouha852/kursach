package ui;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {

    private JLabel statusLabel;

    public StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Статус: Готов");
        add(statusLabel);
    }

    public void setStatus(String message) {
        statusLabel.setText("Статус: " + message);
    }
}
