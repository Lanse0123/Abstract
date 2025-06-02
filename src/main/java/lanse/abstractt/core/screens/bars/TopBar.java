package lanse.abstractt.core.screens.bars;

import lanse.abstractt.core.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class TopBar {

    private static class Exit extends AbstractAction {
        public Exit() {
            super("Exit", UIManager.getIcon("quit"));
            putValue(MNEMONIC_KEY, KeyEvent.VK_E);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public static JMenuBar createMenuBar(Color bgColor, Color fgColor) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setSize(new Dimension(100, 25));
        menuBar.setBackground(bgColor);
        menuBar.setForeground(fgColor);
        menuBar.setBorder(BorderFactory.createLineBorder(ColorPalette.ColorCategory.BUTTONS.getColor()));

        JMenu file = new JMenu("File");
        file.setMnemonic('F');
        file.setOpaque(true);

        Exit e = new Exit();
        file.add(e);
        styleMenu(file, fgColor, bgColor);
        menuBar.add(file);

        return menuBar;
    }

    public static void styleMenu(JMenu menu, Color bgColor, Color fgColor) {
        menu.setBackground(bgColor);
        menu.setForeground(fgColor);
        for (Component comp : menu.getMenuComponents()) {
            if (comp instanceof JMenuItem item) {
                item.setBackground(bgColor);
                item.setForeground(fgColor);
            }
        }
    }
}