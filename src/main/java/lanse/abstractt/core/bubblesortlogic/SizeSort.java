package lanse.abstractt.core.bubblesortlogic;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.bubble.TopBubble;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class SizeSort {

    public static void sort(boolean isFunctionBubble, Bubble[] bubbles) {
        if (!isFunctionBubble) {
            for (Bubble bubble : bubbles) {

                if (bubble instanceof TopBubble) {
                    bubble.scale = 1;
                    return;
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

                        if (startLine == -40404 || endLine == -40404) return;

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
