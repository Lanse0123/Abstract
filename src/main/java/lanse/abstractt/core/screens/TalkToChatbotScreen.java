package lanse.abstractt.core.screens;

import lanse.abstractt.parser.LLMManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.Scanner;

public class TalkToChatbotScreen extends JPanel {

    private final JFrame frame;
    private static boolean mainMenu = true;

    public TalkToChatbotScreen(JFrame frame, Color bgColor) {
        mainMenu = false;
        this.frame = frame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bgColor);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Background text (in red, as requested)
        JLabel infoLabel = new JLabel("<html><font color='red'>Use the Terminal to talk to the chatbot!<br>Click main menu to stop, or enter help for help.</font></html>");
        add(infoLabel);

        System.out.println("Talk to ollama! Enter something below and it will respond. Enter help for help.");

        // Main Menu Button
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.addActionListener(this::mainMenu);

        add(Box.createRigidArea(new Dimension(0, 70)));
        add(mainMenuButton);

        // Start chatbot loop in background
        new Thread(TalkToChatbotScreen::responseLoop).start();
    }

    public static void responseLoop() {
        Scanner scanner = new Scanner(System.in);
        while (!mainMenu) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String prompt = scanner.nextLine().trim();

            if (prompt.equalsIgnoreCase("main menu")) {
                mainMenu = true;
                break;
            }

            if (prompt.isEmpty()) continue;

            Optional<String> response = LLMManager.runLLM(prompt);
            if (response.isPresent()) {
                System.out.println("[Answer] " + response.get());
            } else {
                System.out.println("[Error] No response from LLM.");
            }

            System.out.println("--------");
        }
    }

    private void mainMenu(ActionEvent e) {
        mainMenu = true;
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new MainMenuScreen(frame, getBackground()));
        frame.revalidate();
        frame.repaint();
    }
}