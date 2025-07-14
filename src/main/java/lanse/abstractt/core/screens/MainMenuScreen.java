package lanse.abstractt.core.screens;

import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.storage.AbstractImageManager;
import lanse.abstractt.storage.Settings;
import lanse.abstractt.storage.Storage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Stack;

public class MainMenuScreen extends BackgroundPanel {

    public MainMenuScreen(JFrame frame, Color bgColor) {
        super(AbstractImageManager.getMainMenuBackground().getScaledInstance(2800, 928, Image.SCALE_SMOOTH));
        setBackground(bgColor);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Logo
        ImageIcon logoIcon = AbstractImageManager.getMainTitleIcon();
        Image logoImage = logoIcon.getImage().getScaledInstance(692, 360, Image.SCALE_SMOOTH);
        JLabel title = new JLabel(new ImageIcon(logoImage));
        title.setAlignmentX(CENTER_ALIGNMENT);

        // Buttons
        JButton openWorkspace = createStyledButton("Open Project", () -> openProject(frame, bgColor));
        JButton openRecent = createStyledButton("Open Recent Project", () -> openRecentProject(frame, bgColor));
        JButton settings = createStyledButton("Settings", () -> switchScreen(frame, new SettingsScreen(frame, bgColor)));
        JButton talkToA = createStyledButton("TalkToA", () -> switchScreen(frame, new TalkToChatbotScreen(frame, bgColor)));
        JButton credits = createStyledButton("Credits", () -> switchScreen(frame, new CreditScreen(frame, bgColor)));
        JButton exit = createStyledButton("Exit", () -> System.exit(0));

        // Layout
        add(title);
        add(Box.createRigidArea(new Dimension(0, 30)));
        for (JButton button : List.of(openWorkspace, openRecent, settings, talkToA, credits, exit)) {
            add(button);
            add(Box.createRigidArea(new Dimension(0, 10)));
        }
        add(Box.createVerticalGlue());
    }

    private JButton createStyledButton(String text, Runnable onClick) {
        JButton button = new JButton(text);
        button.setBackground(ColorPalette.ColorCategory.BUTTONS.getColor());
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.addActionListener(e -> onClick.run());
        return button;
    }

    private void openProject(JFrame frame, Color bgColor) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Project Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && !file.getName().equals("AbstractionVisualizerStorage");
            }

            @Override
            public String getDescription() {
                return "Select Project Folder";
            }
        });

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();
            Settings.selectedProjectPath = selectedFolder.getAbsolutePath();
            Settings.addRecentProject(Settings.selectedProjectPath);
            Settings.save();

            Storage.selectedBubblePath = new Stack<>();
            Storage.selectedBubblePath.push(Settings.selectedProjectPath);

            switchScreen(frame, new WorkSpaceScreen(bgColor));
        }
    }

    private void openRecentProject(JFrame frame, Color bgColor) {
        List<String> recent = Settings.getRecentProjects();
        if (recent.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No recent projects found.");
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                frame, "Select a recent project:",
                "Open Recent Project", JOptionPane.PLAIN_MESSAGE,
                null, recent.toArray(), recent.get(0)
        );

        if (selected != null) {
            File selectedFolder = new File(selected);
            if (selectedFolder.exists() && selectedFolder.isDirectory()) {
                Settings.selectedProjectPath = selected;
                Settings.addRecentProject(selected);
                Settings.save();

                Storage.selectedBubblePath = new Stack<>();
                Storage.selectedBubblePath.push(Settings.selectedProjectPath);

                switchScreen(frame, new WorkSpaceScreen(bgColor));
            } else {
                JOptionPane.showMessageDialog(frame, "Selected folder doesn't exist.");
            }
        }
    }

    private void switchScreen(JFrame frame, JPanel screen) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(screen);
        frame.revalidate();
        frame.repaint();
    }
}
