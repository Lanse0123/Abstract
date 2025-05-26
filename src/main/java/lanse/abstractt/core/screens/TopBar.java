package lanse.abstractt.core.screens;

import javax.swing.*;
import java.awt.*;

public class TopBar extends JPanel {

    public static final int HEIGHT = 50;

    public TopBar() {
        setPreferredSize(new Dimension(0, HEIGHT));
        setSize(new Dimension(0, HEIGHT));
        setBackground(new Color(30, 30, 30));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = new JLabel("Abstract Editor");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        add(titleLabel);

        // TODO: Add buttons like "New Project", "Save", etc.
    }
}
