package lanse.abstractt.core.screens.bars;

import lanse.abstractt.Main;
import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.core.WorldMap;
import lanse.abstractt.storage.Storage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class TopBar {

    public static JMenuBar createMenuBar(Color bgColor, Color fgColor) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setSize(new Dimension(100, 25));
        menuBar.setBackground(bgColor);
        menuBar.setForeground(fgColor);
        menuBar.setBorder(BorderFactory.createLineBorder(ColorPalette.ColorCategory.BUTTONS.getColor()));

        JMenu file = new JMenu("File");
        file.setMnemonic('F');
        file.setOpaque(true);

        CloseProject closeProject = new CloseProject();
        ResetCamera resetCamera = new ResetCamera();
        Exit exit = new Exit();

        file.add(closeProject);
        file.add(resetCamera);
        file.add(exit);

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

    private static class ResetCamera extends AbstractAction {
        public ResetCamera() {
            super("Reset Camera", UIManager.getIcon("quit"));
            //putValue(MNEMONIC_KEY, KeyEvent.VK_E); Keybind?
            //putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q")); Keybind?
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO - i need this to also update the screen because currently you need to move your mouse to see the changes.
            WorldMap.setCameraCoordinates(0, 0);
            WorldMap.setZoom(1.0);
        }
    }

    private static class CloseProject extends AbstractAction {
        public CloseProject() {
            super("Close Project", UIManager.getIcon("quit"));
            //putValue(MNEMONIC_KEY, KeyEvent.VK_E); Keybind?
            //putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Q")); Keybind?
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Storage.saveAllBubbles(true, Main.frame);

            Main.createMainMenuScreen();
        }
    }
}