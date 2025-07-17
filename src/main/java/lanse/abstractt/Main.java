package lanse.abstractt;

import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.core.screens.MainMenuScreen;
import lanse.abstractt.parser.LLMManager;
import lanse.abstractt.storage.AbstractImageManager;
import lanse.abstractt.storage.Settings;
import lanse.abstractt.storage.Storage;

import javax.swing.*;
import java.awt.*;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;

public class Main {

//    public static int tickCount;
//    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static JFrame frame;

    public static void main(String[] args) {

        Settings.load();

        SwingUtilities.invokeLater(Main::createMainMenuScreen);

//        // Start tick loop (10 times per second)
//        scheduler.scheduleAtFixedRate(Main::tick, 0, 100, TimeUnit.MILLISECONDS);

        if (!LLMManager.tryStartOllama()) {
            System.err.println("Unable to start ollama, AI features will not work!");
        }
    }

    public static void createMainMenuScreen(){
        if (frame == null) {
            frame = new JFrame("Abstract IDE");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());
            frame.setIconImage(AbstractImageManager.getLogoIcon().getImage());
        }
        else {
            Storage.reset();
            frame.getContentPane().removeAll();
        }

        Storage.setCurrentDepth(0);

        // Pass frame to the screen
        frame.add(new MainMenuScreen(frame, ColorPalette.ColorCategory.PRIMARY_BACKGROUND.getColor()), BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

//    private static void tick() {
//        // This runs 10 times per second
//
//        tickCount++;
//        if (tickCount > 1000000000) tickCount = 0;
//
//        //TODO - make more use of this in the future.
//
//    }
}
