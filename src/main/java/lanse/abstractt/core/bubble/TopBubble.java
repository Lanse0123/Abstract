package lanse.abstractt.core.bubble;

import lanse.abstractt.storage.languages.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopBubble extends Bubble {

    public static HashMap<String, Integer> languageMap = new HashMap<>();
    public static java.util.List<Color> languageColors = new java.util.ArrayList<>();
    public static java.util.List<Float> languagePercents = new java.util.ArrayList<>();
    public static final java.util.List<String> languageExtensions = new ArrayList<>();
    public static int totalFiles = 0;

    public TopBubble(String title, String description, String filePath, boolean isClickable) {
        super(title, description, filePath, isClickable);
        addLanguageBarPlaceholder();
    }

    private void addLanguageBarPlaceholder() {
        JPanel bar = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int x = 0;
                int w = getWidth();
                int h = getHeight();

                for (int i = 0; i < languageExtensions.size(); i++) {
                    String extension = languageExtensions.get(i);
                    int segmentWidth = Math.round(languagePercents.get(i) * w);
                    Color color = languageColors.get(i);
                    g.setColor(color);
                    g.fillRect(x, 0, segmentWidth, h);

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
        languageExtensions.clear();

        // Prepare a cleaned map of extensions -> count
        Map<String, Integer> cleanedMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : languageMap.entrySet()) {
            String key = entry.getKey();
            int count = entry.getValue();

            // Extract true extension (remove anything after '=')
            String ext = key.contains("=") ? key.substring(0, key.indexOf("=")) : key;
            if (!ext.startsWith(".")) ext = "." + ext;

            Color color = LanguageManager.getLanguageColorFromPath(ext, true);
            if (color != null && !color.equals(LanguageManager.UNKNOWN_FILE_COLOR)) {
                cleanedMap.put(ext, cleanedMap.getOrDefault(ext, 0) + count);
            }
            else {
                cleanedMap.put(".unknown", cleanedMap.getOrDefault(".unknown", 0) + count);
            }
        }

        int total = totalFiles == 0 ? 1 : totalFiles;

        for (Map.Entry<String, Integer> entry : cleanedMap.entrySet()) {
            String ext = entry.getKey();
            int count = entry.getValue();

            Color color = LanguageManager.getLanguageColorFromPath(ext, true);
            if (color == null) continue;

            float percent = count / (float) total;
            languageExtensions.add(ext);
            languageColors.add(color);
            languagePercents.add(percent);
        }
    }

}