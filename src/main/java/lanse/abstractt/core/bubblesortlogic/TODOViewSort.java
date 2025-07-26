package lanse.abstractt.core.bubblesortlogic;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.bubble.TopBubble;
import lanse.abstractt.storage.languages.LanguageManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class TODOViewSort {

    public static Map<Bubble, Point> sort(boolean isFunctionBubble, Bubble[] bubbles) {
        if (!isFunctionBubble) {
            for (Bubble bubble : bubbles) {

                if (bubble instanceof TopBubble) {
                    bubble.scale = 1;
                    return null;
                }

                String filePath = bubble.getFilePath();
                File file = new File(filePath);

                long size = getPathSize(file); // Handles files and directories

                bubble.scale = 1 + Math.log(size + 1);
            }
        } else {
            for (Bubble bubble : bubbles) {
                if (bubble instanceof FunctionBubble bubble1) {
                    String filePath = bubble.getFilePath();
                    File file = new File(filePath);

                    try {
                        //TODO - this code could also just about be used to make a CodeBubble of a FunctionBubble
                        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                        int[] span = bubble1.getLineSpan();
                        int startLine = span[0];
                        int endLine = span[1];

                        if (startLine == -40404 || endLine == -40404) return null;

                        int byteCount = 0;
                        for (int i = startLine; i < endLine; i++) {
                            byteCount += lines.get(i).getBytes(StandardCharsets.UTF_8).length;
                        }

                        double sizeInKB = byteCount / 1024.0;
                        bubble.scale = 1 + Math.log(sizeInKB + 1);

                    } catch (IOException e) {
                        System.out.println("Error calculating file size in SizeSort");
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

    private static long getPathSize(File file) {
        if (!file.exists()) return 0;
        if (file.isFile()) return getTODOCount(file);

        long size = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                size += getPathSize(f);
            }
        }
        return size;
    }

    private static long getTODOCount(File file) {
        if (!LanguageManager.isFileParsable(file.getPath())) return 0;

        long count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int index = -1;
                //TODO - this can probably be better
                while ((index = line.indexOf("TODO", index + 1)) != -1) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read file: " + file.getPath());
        }

        return count;
    }
}