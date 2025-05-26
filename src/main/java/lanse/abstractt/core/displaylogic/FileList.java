package lanse.abstractt.core.displaylogic;

import java.awt.*;

public class FileList {

    private static final int VERTICAL_SPACING = 380; // Space between bubbles vertically

    public static Point getNewBubblePosition(int bubbleCount) {
        int x = 100;
        int y = 100 + bubbleCount * VERTICAL_SPACING;
        return new Point(x, y);
    }
}
