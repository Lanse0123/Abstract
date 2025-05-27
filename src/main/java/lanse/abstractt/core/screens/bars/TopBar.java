package lanse.abstractt.core.screens.bars;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TopBar extends JPanel {

    public static final int HEIGHT = 50;

    public TopBar() {
        setPreferredSize(new Dimension(0, HEIGHT));
        setSize(new Dimension(0, HEIGHT));
        setBackground(new Color(30, 30, 30));
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // TODO: Add buttons like "New Project", "Save", etc.

        JButton file = new JButton("File");
        JButton filter = new JButton("Filter");

        styleButton(file);
        styleButton(filter);

        //File
        file.addActionListener((ActionEvent e) -> {
            //TODO - This should open button for Save, Load new project, and maybe more later
        });

        // Open Recent Project
        filter.addActionListener((ActionEvent e) -> {
            //TODO - lets you add new words or file types that should be filtered. You should also be able to remove
            // filtered items. These could maybe be stored in Settings.
        });

        add(file);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(filter);
        add(Box.createRigidArea(new Dimension(0, 10)));
    }

    //kinda like a css stylesheet but java
    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(CENTER_ALIGNMENT);
    }
}
