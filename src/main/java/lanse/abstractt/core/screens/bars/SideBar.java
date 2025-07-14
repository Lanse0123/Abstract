package lanse.abstractt.core.screens.bars;

import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.MiniBubble;
import lanse.abstractt.storage.Storage;

import javax.swing.*;

public class SideBar extends JPanel {
    WorldMap map;

    public SideBar(WorldMap map) {
        this.map = map;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorPalette.ColorCategory.BARS.getColor());
        refresh();
        map.initializeListeners(this);
    }

    // Call this whenever the depth changes or bubbles are added
    public void refresh() {
        removeAll();

        int currentDepth = Storage.getCurrentDepth();

        for (int depth = 0; depth < currentDepth; depth++) {
            int count = Storage.getNumBubblesAtDepth(depth);
            boolean isCurrent = (depth == currentDepth - 1);

            MiniBubble mini = new MiniBubble(depth, count, "Bubble Path", isCurrent);
            add(Box.createVerticalStrut(10));
            add(mini);
        }

        add(Box.createVerticalGlue());
        revalidate();
        repaint();
    }
}
