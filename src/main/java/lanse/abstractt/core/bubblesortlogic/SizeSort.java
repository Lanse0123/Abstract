package lanse.abstractt.core.bubblesortlogic;

import lanse.abstractt.core.bubble.Bubble;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class SizeSort {

    public static void sort(boolean isFunctionBubble, Bubble[] bubbles) {
        if (!isFunctionBubble) {

            for (Bubble bubble : bubbles) {
                String filePath = bubble.getFilePath();
                File file = new File(filePath);

                long size = getPathSize(file); // Handles files and directories

                double sizeInKB = size / 1024.0;
                bubble.scale = 1 + Math.log(sizeInKB + 1); // Logarithmic scaling, min scale = 1
            }

            Arrays.sort(bubbles, Comparator.comparingDouble(b -> -b.scale));

            int x = 0;
            int y = 100;
            for (Bubble bubble : bubbles) {
                int size = (int) (bubble.scale * 40); // base size scaled
                bubble.setLocation(x, y);
                y += size + 20;
            }
        } else {
            // TODO: handle function bubbles later
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
