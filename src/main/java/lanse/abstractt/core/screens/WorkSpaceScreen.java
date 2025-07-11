package lanse.abstractt.core.screens;

import lanse.abstractt.core.displaylogic.DisplayModeSelector;
import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.*;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.core.screens.bars.SideBar;
import lanse.abstractt.core.screens.bars.TopBar;
import lanse.abstractt.storage.Settings;
import lanse.abstractt.storage.Storage;
import lanse.abstractt.storage.StorageCompiler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Stack;

public class WorkSpaceScreen extends JPanel {

    private final WorldMap worldMap = new WorldMap();
    private final SideBar sidebar = new SideBar();
    private final JMenuBar topBar;

    public WorkSpaceScreen(Color bgColor) {
        setBackground(bgColor);
        setLayout(null);

        topBar = TopBar.createMenuBar(bgColor, Color.white);

        add(topBar);
        add(sidebar);

        //JFrame works really weirdly with itself
        ProgressBarPanel.attachTo(this);

        WorldMap.setCameraCoordinates(0, 0);
        worldMap.initializeListeners(this);

        if (Settings.selectedProjectPath != null) {
            Storage.selectedBubblePath = new Stack<>();
            Storage.selectedBubblePath.push(Settings.selectedProjectPath);
            File folder = new File(Settings.selectedProjectPath);
            String projectName = folder.getName();
            String description = "Description: (DO SOMETHING WITH THIS)";

            //Creates the project and saves it to JSON. Starts completely empty.
            StorageCompiler.generateProjectDefaults();

            TopBubble bubble = new TopBubble(projectName, description, Settings.selectedProjectPath);
            Settings.topBubble = bubble;

            try {
                StorageCompiler.waitForRoot();
            }
            catch (InterruptedException ignored) {}
            Storage.load(Settings.selectedProjectPath);
            Storage.setCurrentDepth(1);

            bubble.setSize(bubble.getPreferredSize());
            add(bubble);

        } else {
            JLabel label = new JLabel("No project selected", SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Monospaced", Font.PLAIN, 20));
            label.setBounds(0, 0, 800, 600);
            add(label);
        }
    }

    @Override
    public void doLayout() {
        refreshSidebar();

        topBar.setBounds(0, 0, getWidth(), topBar.getHeight());

        int SIDEBAR_WIDTH = 200;
        sidebar.setBounds(0, topBar.getHeight(), SIDEBAR_WIDTH, getHeight() - topBar.getHeight());

        JPanel progressBar = ProgressBarPanel.getPanel();
        if (progressBar.getParent() == this && progressBar.isVisible()) {
            progressBar.setBounds(0, getHeight() - ProgressBarPanel.HEIGHT, getWidth(), ProgressBarPanel.HEIGHT);
        }

        DisplayModeSelector.clearBubbles();
        for (Component comp : getComponents()) {
            //TODO - for each new bubble type, add it to this if statement
            if (comp instanceof Bubble || comp instanceof CodeBubble) {
                Point pos = DisplayModeSelector.getNewBubblePosition();
                double worldX = pos.x;
                double worldY = pos.y;
                Point screenPos = worldMap.transform(worldX, worldY);

                double zoom = worldMap.getZoom();
                int scaledWidth = (int) (comp.getPreferredSize().width * zoom);
                int scaledHeight = (int) (comp.getPreferredSize().height * zoom);

                // Offset bubbles by sidebar width so they don’t appear under it
                comp.setBounds(screenPos.x + SIDEBAR_WIDTH, screenPos.y, scaledWidth, scaledHeight);

                //TODO - this 1 else if statement messed up all the Bars I have. Look into this
            } else if (comp != sidebar && comp != topBar && comp != progressBar) {
                // fallback for static elements
                comp.setBounds(SIDEBAR_WIDTH, 0, getWidth() - SIDEBAR_WIDTH, getHeight());
            }
        }
    }

    public void removeBubbles() {
        Component[] componentList = getComponents();
        for (Component component : componentList) {
            if (component instanceof Bubble || component instanceof CodeBubble) {
                remove(component);
            }
        }
    }

    public void refreshSidebar() {
        sidebar.refresh();
    }
}
