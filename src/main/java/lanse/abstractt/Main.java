package lanse.abstractt;

import lanse.abstractt.core.screens.MainMenuScreen;
import lanse.abstractt.storage.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static int tickCount;
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {

        Settings.load();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Abstract IDE");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            Color bgColor = new Color(40, 40, 40); // Dark gray

            // Pass frame to the screen
            frame.add(new MainMenuScreen(frame, bgColor), BorderLayout.CENTER);

            frame.setVisible(true);
        });

        // Start server tick loop (10 times per second)
        scheduler.scheduleAtFixedRate(Main::serverTick, 0, 100, TimeUnit.MILLISECONDS);
    }

    public static void serverTick() {
        // This runs 10 times per second, just like normal minecraft java redstone ticks.

        tickCount++;
        if (tickCount > 1000000000) tickCount = 0;

        //TODO - its already somehow laggy. Fix it. This might help, though I am not sure what I need this for yet.


    }
}
