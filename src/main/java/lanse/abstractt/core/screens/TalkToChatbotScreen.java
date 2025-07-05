package lanse.abstractt.core.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TalkToChatbotScreen extends JPanel {

    private final JFrame frame;

    public TalkToChatbotScreen(JFrame frame, Color bgColor) {
        this.frame = frame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bgColor);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        //TODO - add text for the background saying:
        // Use the Terminal to talk to the chatbot!
        // Click main menu to stop, or enter help for help.

        // Main Menu Button
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.addActionListener(this::mainMenu);

        add(mainMenuButton);
        add(Box.createRigidArea(new Dimension(0, 70)));
    }

    private void mainMenu(ActionEvent e){
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new MainMenuScreen(frame, getBackground()));
        frame.revalidate();
        frame.repaint();
    }
}
