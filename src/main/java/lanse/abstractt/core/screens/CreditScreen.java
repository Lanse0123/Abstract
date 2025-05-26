package lanse.abstractt.core.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CreditScreen extends JPanel {

    private final JFrame frame;

    //TODO - THANK YOU JAMES!!!

    // might put things like link to my discord here and stuff
    // also might add liscence here that says like dont steal this to make profit, and if any suggestions or want to help, join my discord
    // also also might put patreon / donation link here eventually

    public CreditScreen(JFrame frame, Color bgColor) {
        this.frame = frame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bgColor);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Save Button
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.addActionListener(this::mainMenu);

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(mainMenuButton);
    }

    private void mainMenu(ActionEvent e){
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new MainMenuScreen(frame, getBackground()));
        frame.revalidate();
        frame.repaint();
    }

}
