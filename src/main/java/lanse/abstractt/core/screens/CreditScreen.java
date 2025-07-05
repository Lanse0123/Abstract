package lanse.abstractt.core.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

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

        //TODO - add text next to icon
        ImageIcon lanseLogoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/credits/LanseIcon.png")));
        Image image = lanseLogoIcon.getImage().getScaledInstance(161, 151, Image.SCALE_SMOOTH);
        lanseLogoIcon = new ImageIcon(image);
        JLabel logo = new JLabel(lanseLogoIcon);
        logo.setAlignmentX(LEFT_ALIGNMENT);

        //TODO - add text next to icon
        ImageIcon jamesLogoIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/credits/JamesIcon.png")));
        Image image2 = jamesLogoIcon.getImage().getScaledInstance(146, 146, Image.SCALE_SMOOTH);
        jamesLogoIcon = new ImageIcon(image2);
        JLabel logo2 = new JLabel(jamesLogoIcon);
        logo2.setAlignmentX(LEFT_ALIGNMENT);

        //TODO - add text next to icon
        ImageIcon ytIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/credits/ytIcon.png")));
        Image image3 = ytIcon.getImage().getScaledInstance(168, 116, Image.SCALE_SMOOTH);
        ytIcon = new ImageIcon(image3);
        JLabel logo3 = new JLabel(ytIcon);
        logo3.setAlignmentX(LEFT_ALIGNMENT);

        //TODO - add text next to icon
        ImageIcon discordIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/credits/discordIcon.png")));
        Image image4 = discordIcon.getImage().getScaledInstance(170, 111, Image.SCALE_SMOOTH);
        discordIcon = new ImageIcon(image4);
        JLabel logo4 = new JLabel(discordIcon);
        logo4.setAlignmentX(LEFT_ALIGNMENT);

        // Main Menu Button
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.addActionListener(this::mainMenu);

        add(mainMenuButton);
        add(Box.createRigidArea(new Dimension(0, 70)));
        add(logo);
        add(Box.createRigidArea(new Dimension(0, 0)));
        add(logo2);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(logo3);
        add(Box.createRigidArea(new Dimension(0, 15)));
        add(logo4);
        add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void mainMenu(ActionEvent e){
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new MainMenuScreen(frame, getBackground()));
        frame.revalidate();
        frame.repaint();
    }

}
