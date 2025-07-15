package lanse.abstractt.core.screens;

import lanse.abstractt.storage.AbstractImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.Objects;

public class CreditScreen extends BackgroundPanel {

    private final JFrame frame;

    //TODO - I need to unsquish some of the icons
    private static final int ICON_SIZE = 100;

    private static final String[] URLS = {
            "https://www.youtube.com/@lanse012",
            "https://github.com/jamesMFelder",
            "https://www.youtube.com/watch?v=byYseUyndIw&list=PLLRPIrDElQHdqIyZXCS2L0IQfGZRpvqqp",
            "https://discord.gg/qsbJrBdw5V",
            "https://github.com/Lanse0123/Abstract"
    };

    private static final String[] ICON_PATHS = {
            "/images/credits/LanseIcon.png",
            "/images/credits/JamesIcon.png",
            "/images/credits/ytIcon.png",
            "/images/credits/discordIcon.png",
            "/images/abstract/Abstract Logo Icon.png"
    };

    private String getLabelForUrl(String url) {
        if (url.contains("youtube.com/@lanse012")) return "Lanse";
        if (url.contains("github.com/jamesMFelder")) return "James";
        if (url.contains("youtube.com/watch")) return "Abstract Devlog";
        if (url.contains("discord.gg")) return "Join our Discord Server!";
        if (url.contains("github.com/Lanse0123")) return "Abstract Source Code";
        return url;
    }

    public CreditScreen(JFrame frame, Color bgColor) {
        super(AbstractImageManager.getCreditsBackground().getImage());

        this.frame = frame;
        setBackground(bgColor);
        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));

        // Main Menu Button
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setBounds(20, 20, 120, 30);
        mainMenuButton.addActionListener(e -> switchToMainMenu());
        add(mainMenuButton);

        // Create and place icons around a pentagon
        int n = ICON_PATHS.length;
        int w = getPreferredSize().width;
        int h = getPreferredSize().height;
        int cx = w / 2;
        int cy = h / 2;
        int radius = Math.min(w, h) / 3;

        //Math hell to make a pentagon
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2 + i * 2 * Math.PI / n;
            int x = cx + (int) (Math.cos(angle) * radius) - ICON_SIZE / 2 + 100;
            int y = cy + (int) (Math.sin(angle) * radius) - ICON_SIZE / 2 + 50;

            ImageIcon icon = loadScaledIcon(ICON_PATHS[i], ICON_SIZE, ICON_SIZE);
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setBounds(x, y, ICON_SIZE, ICON_SIZE);
            final String url = URLS[i];
            iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            iconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                            Desktop.getDesktop().browse(new URI(url));
                        }
                        else {
                            Runtime.getRuntime().exec(new String[] {"xdg-open", url});
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            add(iconLabel);

            // Text next to icon
            JLabel textLabel = new JLabel(getLabelForUrl(url));
            textLabel.setForeground(Color.WHITE);
            textLabel.setBounds(x + ICON_SIZE + 10, y + ICON_SIZE / 4, 200, 20);
            add(textLabel);
        }
    }

    private ImageIcon loadScaledIcon(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void switchToMainMenu() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new MainMenuScreen(frame, getBackground()));
        frame.revalidate();
        frame.repaint();
    }
}