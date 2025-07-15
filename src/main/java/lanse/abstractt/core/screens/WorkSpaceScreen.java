package lanse.abstractt.core.screens;

import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.*;
import lanse.abstractt.core.bubblesortlogic.BubbleSorter;
import lanse.abstractt.core.displaylogic.DisplayModeSelector;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.core.screens.bars.SideBar;
import lanse.abstractt.core.screens.bars.TopBar;
import lanse.abstractt.storage.Settings;
import lanse.abstractt.storage.Storage;
import lanse.abstractt.storage.StorageCompiler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
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

        Storage.load(Settings.selectedProjectPath, true);
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

        Component[] comps = getComponents();
        java.util.List<Bubble> visualBubbles = new ArrayList<>();

        for (Component comp : comps) {
            if (isVisualBubble(comp)) {
                visualBubbles.add((Bubble) comp);
            } else if (comp != sidebar && comp != topBar && comp != progressBar) {
                comp.setBounds(SIDEBAR_WIDTH, 0, getWidth() - SIDEBAR_WIDTH, getHeight());
            }
        }

        //TODO - this is being called a LOT, this probably is the laggiest part of Abstract
        layoutAllBubbles(visualBubbles.toArray(new Bubble[0]));
    }

    public void layoutAllBubbles(Bubble[] allBubbles) {
        Map<Bubble, Point> layoutMap = DisplayModeSelector.getBubbleLayout(allBubbles);

        BubbleSorter.sort(allBubbles);

        double zoom = worldMap.getZoom();

        for (Bubble bubble : allBubbles) {
            Point worldPos = layoutMap.getOrDefault(bubble, new Point(0, 0));
            Point screenPos = worldMap.transform(worldPos.x, worldPos.y);

            int width = (int) (bubble.getPreferredSize().width * bubble.scale * zoom);
            int height = (int) (bubble.getPreferredSize().height * bubble.scale * zoom);

            bubble.setBounds(screenPos.x + SIDEBAR_WIDTH, screenPos.y, width, height);
        }
    }

    private boolean isVisualBubble(Component comp) {
        return comp instanceof Bubble || comp instanceof PictureBubble || comp instanceof CodeBubble;
    }

    public void refreshSidebar() {
        sidebar.refresh();
    }
}
