package lanse.abstractt.core.screens;

import javax.swing.*;

public class ScreenHandler {

    public static void switchScreen(JFrame frame, JPanel screen) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(screen);
        frame.revalidate();
        frame.repaint();
    }
}
