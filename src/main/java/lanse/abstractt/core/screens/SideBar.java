package lanse.abstractt.core.screens;

import lanse.abstractt.core.bubble.MiniBubble;
import lanse.abstractt.storage.Storage;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SideBar extends JPanel {

    public SideBar() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(30, 30, 30));
        refresh();
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
