package lanse.abstractt.core.bubble;

import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubblesortlogic.BubbleSorter;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.core.screens.WorkSpaceScreen;
import lanse.abstractt.storage.Storage;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class MiniBubble extends JPanel {

    private final boolean isCurrent;
    private final Color fillColor;
    private final int depth;
    private String bubblePath; //TODO - make use of this if you want, idk if I will

    public MiniBubble(int depth, int count, String path, boolean isCurrent) {
        this.isCurrent = isCurrent;
        this.depth = depth;
        //TODO - make this use the language color of the bubble its from.
        this.fillColor = isCurrent ? ColorPalette.ColorCategory.SUCCESS.getColor() : ColorPalette.ColorCategory.BUBBLES_AND_PROGRESS.getColor();
        this.bubblePath = path;

        setPreferredSize(new Dimension(60, 40));
        setMaximumSize(new Dimension(60, 40));
        setOpaque(false);  // important for custom painting

        setLayout(new BorderLayout());

        JLabel label = new JLabel(String.valueOf(count), SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        // Add click behavior only if this isn't the current depth
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                handleClick();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape oval = new Ellipse2D.Double(0, 0, getWidth(), getHeight());
        g2.setClip(oval);

        // Fill with light blue
        //TODO - I might eventually make the color dynamic using the same color that languages use
        g2.setColor(fillColor);
        g2.fill(oval);

        // Outline
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(4));
        g2.draw(oval);

        g2.dispose();
        super.paintComponent(g);
    }

    private void handleClick() {
        if (isCurrent || ProgressBarPanel.isLoading()) return;
        if (Bubble.isABubbleBeingEdited) return;

        // Set current depth
        Storage.setCurrentDepth(depth);

        // Refresh parent (bubble screen)
        //TODO - something might be wrong about this
        Container parent = getParent();

        while (parent != null && !(parent instanceof WorkSpaceScreen)) {
            parent = parent.getParent();
        }

        if (parent instanceof WorkSpaceScreen workspace) {
            Storage.saveAllBubbles(true, workspace);

            //forced to use java.util here since java.awt also has Lists that work differently
            java.util.List<Bubble> bubbles = Storage.getBubblesAtCurDepth();
            //TODO - this loop is definitely doing something wrong
            for (Bubble bubble : bubbles) {
                workspace.setLayout(null);
                workspace.add(bubble);
            }

            WorldMap.setCameraCoordinates(0, 0);

            workspace.refreshSidebar(); // Also updates MiniBubbles
            workspace.revalidate();
            workspace.repaint();
            BubbleSorter.isSorted = false;
        }
    }
}
