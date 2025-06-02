package lanse.abstractt.core.screens;

import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.storage.Settings;
import lanse.abstractt.storage.Storage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Objects;
import java.util.Stack;

public class MainMenuScreen extends JPanel {
    public MainMenuScreen(JFrame frame, Color bgColor) {
        setBackground(bgColor);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/Abstract Logo 1.png")));
        Image image = logoIcon.getImage().getScaledInstance(692, 360, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(image);
        JLabel title = new JLabel(logoIcon);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JButton openWorkspace = new JButton("Open Project");
        JButton openRecent = new JButton("Open Recent Project");
        JButton settings = new JButton("Settings");
        JButton credits = new JButton("Credits");
        JButton exit = new JButton("Exit");

        styleButton(openWorkspace);
        styleButton(openRecent);
        styleButton(settings);
        styleButton(credits);
        styleButton(exit);

        //Open new Project
        openWorkspace.addActionListener((ActionEvent e) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Project Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(java.io.File file){
                    return file.isDirectory() && !(file.getName().equals("AbstractionVisualizerStorage"));
                }

                public String getDescription(){
                    return "Select Project Folder";
                }
            });

            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = chooser.getSelectedFile();
                Settings.selectedProjectPath = selectedFolder.getAbsolutePath();

                //Add to recent project map
                Settings.addRecentProject(Settings.selectedProjectPath);
                Settings.save();

                Storage.selectedBubblePath = new Stack<>();
                Storage.selectedBubblePath.push(Settings.selectedProjectPath);

                // Switch to workspace screen
                frame.getContentPane().removeAll();
                frame.getContentPane().add(new WorkSpaceScreen(bgColor));
                frame.revalidate();
                frame.repaint();
            }
        });

        // Open Recent Project
        openRecent.addActionListener((ActionEvent e) -> {
            java.util.List<String> recent = Settings.getRecentProjects();
            if (recent.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No recent projects found.");
                return;
            }

            // Build selection dialog
            String selected = (String) JOptionPane.showInputDialog(frame, "Select a recent project:",
                    "Open Recent Project", JOptionPane.PLAIN_MESSAGE, null, recent.toArray(), recent.get(0));

            if (selected != null) {
                File selectedFolder = new File(selected);
                if (selectedFolder.exists() && selectedFolder.isDirectory()) {
                    Settings.selectedProjectPath = selected;
                    Settings.addRecentProject(selected); // move to top again
                    Settings.save(); // Save updated list

                    Storage.selectedBubblePath = new Stack<>();
                    Storage.selectedBubblePath.push(Settings.selectedProjectPath);

                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(new WorkSpaceScreen(bgColor));
                    frame.revalidate();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Selected folder doesn't exist.");
                }
            }
        });

        //Open Settings Window
        settings.addActionListener((ActionEvent e) -> {

            // Switch to settings screen
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new SettingsScreen(frame, bgColor));
            frame.revalidate();
            frame.repaint();

        });

        //Open Settings Window
        credits.addActionListener((ActionEvent e) -> {

            // Switch to settings screen
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new CreditScreen(frame, bgColor));
            frame.revalidate();
            frame.repaint();

        });

        exit.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        add(title);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(openWorkspace);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(openRecent);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(settings);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(credits);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(exit);

        //funni name. It adds space around it. Here, it adds space from the bottom of the screen. If its above the add blocks,
        // then it would make space above those.
        add(Box.createVerticalGlue());
    }

    //kinda like a css stylesheet but java
    private void styleButton(JButton button) {
        button.setBackground(ColorPalette.ColorCategory.BUTTONS.getColor());
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(CENTER_ALIGNMENT);
    }
}
