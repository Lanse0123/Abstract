package lanse.abstractt.core.screens;

import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.*;
import lanse.abstractt.core.displaylogic.DisplayModeSelector;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.core.screens.bars.SideBar;
import lanse.abstractt.core.screens.bars.TopBar;
import lanse.abstractt.storage.Settings;
import lanse.abstractt.storage.Storage;
import lanse.abstractt.storage.StorageCompiler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Set;
import java.util.Stack;

public class WorkSpaceScreen extends JPanel {

    private static final int SIDEBAR_WIDTH = 200;

    private final WorldMap worldMap = new WorldMap();
    private final SideBar sidebar = new SideBar(worldMap);
    private final JMenuBar topBar;

    public WorkSpaceScreen(Color bgColor) {
        setBackground(bgColor);
        setLayout(null);

        topBar = TopBar.createMenuBar(bgColor, Color.WHITE);
        add(topBar);
        add(sidebar);
        ProgressBarPanel.attachTo(this);

        WorldMap.setCameraCoordinates(0, 0);
        worldMap.initializeListeners(this);

        if (Settings.selectedProjectPath != null) {
            initProject();
        } else {
            displayNoProjectMessage();
        }
    }

    private void initProject() {
        Storage.selectedBubblePath = new Stack<>();
        Storage.selectedBubblePath.push(Settings.selectedProjectPath);

        StorageCompiler.generateProjectDefaults();

        TopBubble bubble = Storage.loadTopBubble();
        Settings.topBubble = bubble;

        try {
            StorageCompiler.waitForRoot();
        } catch (InterruptedException ignored) {}

        Storage.load(Settings.selectedProjectPath);
        Storage.setCurrentDepth(1);

        bubble.setSize(bubble.getPreferredSize());
        add(bubble);
    }

    private void displayNoProjectMessage() {
        JLabel label = new JLabel("No project selected", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.PLAIN, 20));
        label.setBounds(0, 0, 800, 600);
        add(label);
    }

    @Override
    public void doLayout() {
        refreshSidebar();

        topBar.setBounds(0, 0, getWidth(), topBar.getHeight());
        sidebar.setBounds(0, topBar.getHeight(), SIDEBAR_WIDTH, getHeight() - topBar.getHeight());

        JPanel progressBar = ProgressBarPanel.getPanel();
        if (progressBar.getParent() == this && progressBar.isVisible()) {
            progressBar.setBounds(0, getHeight() - ProgressBarPanel.HEIGHT, getWidth(), ProgressBarPanel.HEIGHT);
        }

        DisplayModeSelector.clearBubbles();

        for (Component comp : getComponents()) {
            if (isVisualBubble(comp)) {
                layoutBubble(comp);
            } else if (comp != sidebar && comp != topBar && comp != progressBar) {
                // fallback layout
                comp.setBounds(SIDEBAR_WIDTH, 0, getWidth() - SIDEBAR_WIDTH, getHeight());
            }
        }
    }

    private void layoutBubble(Component comp) {
        boolean isCode = comp instanceof CodeBubble;
        Point worldPos = DisplayModeSelector.getNewBubblePosition(isCode);
        Point screenPos = worldMap.transform(worldPos.x, worldPos.y);
        double zoom = worldMap.getZoom();

        int width = (int) (comp.getPreferredSize().width * zoom);
        int height = (int) (comp.getPreferredSize().height * zoom);

        comp.setBounds(screenPos.x + SIDEBAR_WIDTH, screenPos.y, width, height);
    }

    private boolean isVisualBubble(Component comp) {
        return comp instanceof Bubble || comp instanceof PictureBubble || comp instanceof CodeBubble;
    }

    public void removeBubbles() {
        //this is such a smart way of doing this, I didnt realize you could have a Set of classes...
        Set<Class<?>> bubbleTypes = Set.of(Bubble.class, CodeBubble.class, PictureBubble.class, BubbleBridge.class);
        for (Component comp : getComponents()) {
            if (bubbleTypes.contains(comp.getClass())) {
                remove(comp);
            }
        }
    }

    public void refreshSidebar() {
        sidebar.refresh();
    }
}
