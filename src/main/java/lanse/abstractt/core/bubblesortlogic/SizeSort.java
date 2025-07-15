package lanse.abstractt.core.bubblesortlogic;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.bubble.TopBubble;

import java.io.File;

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
            for (Bubble bubble : bubbles){
                if (bubble instanceof FunctionBubble bubble1) {
                    String filePath = bubble.getFilePath();
                    File file = new File(filePath);

                    //TODO - i need to get the lines the bubble is ranging, and get the size of those lines
                    bubble1.getLineSpan();

                    long size = getPathSize(file); // Handles files and directories
                    double sizeInKB = size / 1024.0;

                    bubble.scale = 1 + Math.log(sizeInKB + 1);
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
