package lanse.abstractt.core.screens.bars;

import lanse.abstractt.core.ColorPalette;

import javax.swing.*;
import java.awt.*;

public class ProgressBarPanel {

    private static boolean loading = false;
    private static int progress = 0;
    private static Container parent = null;
    public static final int HEIGHT = 80;
    private static String statusText = "";

    private static final JPanel panel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            Color background = ColorPalette.ColorCategory.BARS.getColor();
            g2.setColor(background);
            g2.fillRect(0, 0, width, height);

            int filledWidth = 0;
            if (loading) {
                g2.setColor(ColorPalette.ColorCategory.BUBBLES_AND_PROGRESS.getColor());
                filledWidth = (int) (width * (progress / 100.0));
                g2.fillRect(0, 0, filledWidth, height);
            }

            // Draw status text
            if (!statusText.isEmpty()) {
                g2.setFont(new Font("Monospaced", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(statusText);
                int textHeight = fm.getHeight();

                int textX = (width - textWidth) / 2;
                int textY = (height - textHeight) / 2 + fm.getAscent();

                // this probably does something cool
                Color overColor = (textX + textWidth / 2 < filledWidth) ? ColorPalette.ColorCategory.BUBBLES_AND_PROGRESS.getColor() : background;
                Color textColor = new Color(255 - overColor.getRed(), 255 - overColor.getGreen(), 255 - overColor.getBlue());
                g2.setColor(textColor);
                g2.drawString((statusText + ": " + progress + "%"), textX, textY);
            }
        }
    };

    static {
        panel.setPreferredSize(new Dimension(0, HEIGHT));
        panel.setSize(new Dimension(0, HEIGHT));
        panel.setVisible(false);
    }

    public static void attachTo(Container newParent) {
        parent = newParent;
        parent.add(panel);
    }

    public static void setProgress(double progress) {
        int percent = (int) (progress * 100);
        ProgressBarPanel.progress = percent;
    }

    public static void show() {
        loading = true;
        panel.setVisible(true);
        if (parent != null) parent.revalidate();
    }

    public static void hide() {
        loading = false;
        panel.setVisible(false);
        if (parent != null) parent.revalidate();
    }

    public static boolean isLoading() {
        return loading;
    }

    public static JPanel getPanel() {
        return panel;
    }

    public static void setLoading(boolean loading, String text){
        ProgressBarPanel.loading = loading;
        statusText = text;
    }
}
