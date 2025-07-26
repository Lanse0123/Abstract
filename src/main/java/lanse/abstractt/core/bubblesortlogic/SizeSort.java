package lanse.abstractt.core.bubblesortlogic;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.bubble.TopBubble;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class SizeSort {

    public static Map<Bubble, Point> sort(boolean isFunctionBubble, Bubble[] bubbles, String sizeOrder) {
        if (!isFunctionBubble) {
            for (Bubble bubble : bubbles) {

                if (bubble instanceof TopBubble) {
                    bubble.scale = 1;
                    return null;
                }

                String filePath = bubble.getFilePath();
                File file = new File(filePath);

                long size = getPathSize(file); // Handles files and directories
                double sizeInKB = size / 1024.0;

                bubble.scale = 1 + Math.log(sizeInKB + 1);
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

        if (sizeOrder != null){
            if (sizeOrder.equals("lts")){
                Arrays.sort(bubbles, Comparator.comparingDouble(b -> -b.scale));
            } else if (sizeOrder.equals("stl")){
                Arrays.sort(bubbles, Comparator.comparingDouble(b -> b.scale));
            }
        }

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
        if (file.isFile()) return file.length();

        long size = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                size += getPathSize(f);
            }
        }
        return size;
    }
}