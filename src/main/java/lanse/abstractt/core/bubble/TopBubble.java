package lanse.abstractt.core.bubble;

import lanse.abstractt.storage.languages.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class TopBubble extends Bubble {

    public static HashMap<String, Integer> languageMap = new HashMap<>();
    public static java.util.List<Color> languageColors = new java.util.ArrayList<>();
    public static java.util.List<Float> languagePercents = new java.util.ArrayList<>();
    public static int totalFiles = 0;

    public TopBubble(String title, String description, String filePath) {
        super(title, description, filePath);
        addLanguageBarPlaceholder();
    }

    private void addLanguageBarPlaceholder() {
        JPanel bar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int x = 0;
                int w = getWidth();
                int h = getHeight();

                int i = 0;
                for (String extension : languageMap.keySet()) {
                    int segmentWidth = Math.round(languagePercents.get(i) * w);
                    Color color = languageColors.get(i);
                    g.setColor(color);
                    g.fillRect(x, 0, segmentWidth, h);

                    // Draw the language extension in the center of the bar segment
                    g.setColor(getContrastingColor(color));
                    String label = extension.startsWith(".") ? extension.substring(1) : extension;
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(label);
                    int textHeight = fm.getAscent();

                    int textX = x + (segmentWidth - textWidth) / 2;
                    int textY = (h + textHeight) / 2 - 2;

                    if (segmentWidth > textWidth + 4) {
                        g.drawString(label, textX, textY);
                    }

                    x += segmentWidth;
                    i++;
                }
            }
        };
        bar.setPreferredSize(new Dimension(width, 15));
        this.add(bar, BorderLayout.SOUTH);
    }

    private static Color getContrastingColor(Color color) {
        int brightness = (int) Math.sqrt(
                color.getRed() * color.getRed() * 0.241 +
                        color.getGreen() * color.getGreen() * 0.691 +
                        color.getBlue() * color.getBlue() * 0.068);
        return brightness < 130 ? Color.WHITE : Color.BLACK;
    }

    public static void calculateLanguageBar() {
        languageColors.clear();
        languagePercents.clear();

        int total = totalFiles == 0 ? 1 : totalFiles;

        //filter out extensions that aren't in the list of languages
        languageMap.entrySet().removeIf(entry -> LanguageManager.getLanguageColorFromPath(entry.getKey()) == null);

        for (String extension : languageMap.keySet()) {
            Color color = LanguageManager.getLanguageColorFromPath(extension);

            if (color == null) continue;

            float percent = languageMap.get(extension) / (float) total;
            languageColors.add(color);
            languagePercents.add(percent);
        }
    }


}