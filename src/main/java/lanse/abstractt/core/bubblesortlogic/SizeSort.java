package lanse.abstractt.core.bubblesortlogic;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.storage.Storage;

public class SizeSort {

    public static void sort(boolean isFunctionBubble) {

        Bubble[] bubbles = Storage.getAllBubbles();

        for (Bubble bubble : bubbles){
            String filePath = bubble.getFilePath();
            //TODO - get size of file at filepath and save it to a list.
            // Then sort the list by size, and bubble.scale = fileSize, with a minimum of 1. 2 is 2x scale, 10 is 10x scale vs default.
            // Make sure to space the bubbles out enough so they don't overlap
        }

        if (!isFunctionBubble){

        } else {

        }
    }
}
