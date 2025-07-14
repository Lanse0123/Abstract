package lanse.abstractt.core.screens;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private final Image background;

    //TODO - i hate that this uses a class to work. I want to change this
    public BackgroundPanel(Image background) {
        this.background = background;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    //TODO - this is squished, I need it to not be squished or something
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
