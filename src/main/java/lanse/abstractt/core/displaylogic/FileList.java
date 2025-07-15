package lanse.abstractt.core.displaylogic;

import lanse.abstractt.core.bubble.Bubble;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FileList {

    private static final int VERTICAL_SPACING = 380;
    private static final int X_OFFSET = 100;
    private static final int Y_START = 100;

    public static Map<Bubble, Point> getLayout(Bubble[] bubbles) {
        Map<Bubble, Point> layout = new HashMap<>();

        for (int i = 0; i < bubbles.length; i++) {
            Bubble bubble = bubbles[i];
            int x = X_OFFSET;
            int y = Y_START + i * VERTICAL_SPACING;
            layout.put(bubble, new Point(x, y));
        }

        return layout;
    }
}
