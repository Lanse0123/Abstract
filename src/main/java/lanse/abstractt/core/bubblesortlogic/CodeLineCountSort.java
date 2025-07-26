package lanse.abstractt.core.bubblesortlogic;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.bubble.TopBubble;
import lanse.abstractt.storage.languages.LanguageManager;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class CodeLineCountSort {

    public static Map<Bubble, Point> sort(boolean isFunctionBubble, Bubble[] bubbles) {
        if (!isFunctionBubble) {
            for (Bubble bubble : bubbles) {
                if (bubble instanceof TopBubble) {
                    bubble.scale = 1;
                    return null;
                }

                String filePath = bubble.getFilePath();
                File file = new File(filePath);

                long lineCount = getTotalCodeLines(file);
                System.out.println("Lines of code in visible paths: " + lineCount);

                bubble.scale = 1 + Math.log(lineCount + 1);
            }
        } else {
            for (Bubble bubble : bubbles) {
                if (bubble instanceof FunctionBubble bubble1) {
                    String filePath = bubble.getFilePath();
                    File file = new File(filePath);

                    try {
                        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                        int[] span = bubble1.getLineSpan();
                        int startLine = span[0];
                        int endLine = span[1];

                        if (startLine == -40404 || endLine == -40404) return null;

                        int codeLineCount = 0;
                        for (int i = startLine; i < endLine && i < lines.size(); i++) {
                            String line = lines.get(i).trim();
                            if (!line.isEmpty()) {
                                codeLineCount++;
                            }
                        }

                        bubble.scale = 1 + Math.log(codeLineCount + 1);

                    } catch (IOException e) {
                        System.out.println("Error calculating line count in CodeLineCountSort");
                        bubble.scale = 1;
                    }
                }
            }
        }

        // Bubbles are repositioned below.
        Map<Bubble, Point> layout = new HashMap<>();

        int spacing = 100;
        int screenHeight = 800;

        int totalHeight = 0;
        for (Bubble b : bubbles) {
            int scaledHeight = (int) (b.getPreferredSize().height * b.scale);
            totalHeight += scaledHeight + spacing;
        }
        totalHeight -= spacing;

        int currentY = (screenHeight - totalHeight) / 2;

        for (Bubble b : bubbles) {
            int scaledHeight = (int) (b.getPreferredSize().height * b.scale);
            layout.put(b, new Point((int) (-scaledHeight / 1.125) + 100, currentY));
            currentY += scaledHeight + spacing;
        }

        return layout;
    }

    private static long getTotalCodeLines(File file) {
        if (!file.exists()) return 0;
        if (file.isFile()) return countCodeLines(file);

        long count = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                count += getTotalCodeLines(f);
            }
        }
        return count;
    }

    private static long countCodeLines(File file) {
        if (!LanguageManager.isFileParsable(file.getPath())) return 0;

        long count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read file: " + file.getPath());
        }

        return count;
    }
}
