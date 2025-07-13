package lanse.abstractt;

import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.core.screens.MainMenuScreen;
import lanse.abstractt.parser.LLMManager;
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

//        ProcessBuilder process = new ProcessBuilder("C:\\Users\\mrsde\\OneDrive\\Desktop\\jdt-language-server-1.31.0-202401111522\\bin\\jdtls.bat");
//        process.start();

        Settings.load();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Abstract IDE");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            // Pass frame to the screen
            frame.add(new MainMenuScreen(frame, ColorPalette.ColorCategory.PRIMARY_BACKGROUND.getColor()), BorderLayout.CENTER);

            frame.setVisible(true);
        });

        // Start server tick loop (10 times per second)
        scheduler.scheduleAtFixedRate(Main::serverTick, 0, 100, TimeUnit.MILLISECONDS);

        if (!LLMManager.tryStartOllama()) {
            System.err.println("Unable to start ollama, AI features will not work!");
        }
    }

    public static void serverTick() {
        // This runs 10 times per second, just like normal minecraft java redstone ticks.

        tickCount++;
        if (tickCount > 1000000000) tickCount = 0;

        //TODO - make more use of this in the future.

    }
}
