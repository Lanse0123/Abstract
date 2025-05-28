package lanse.abstractt.core.bubble;

import javax.swing.*;
import java.awt.*;

public class TopBubble extends Bubble {
    public TopBubble(String title, String description, String filePath) {
        super(title, description, filePath);
        addLanguageBarPlaceholder();
    }

    //TODO - figure out icon stuff so I can get the icon, get the average color of the icon pixels, and use that as the color.
    private void addLanguageBarPlaceholder() {
        JPanel bar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Placeholder red bar until it gets the average language colors and stuff
                g.setColor(Color.RED);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setPreferredSize(new Dimension(width, 15));
        this.add(bar, BorderLayout.SOUTH);
    }
}