package lanse.abstractt.core.displaylogic;

import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.screens.WorkSpaceScreen;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FileList {

    private static final int VERTICAL_SPACING = 380;
    private static final int X_OFFSET = 100;
    private static final int Y_START = 100;

    public static Map<Bubble, Point> getLayout(Bubble[] bubbles, Component[] staticBubbles, WorldMap worldMap) {
        Map<Bubble, Point> layout = new HashMap<>();

        for (int i = 0; i < bubbles.length; i++) {
            Bubble bubble = bubbles[i];
            int x = X_OFFSET;
            int y = Y_START + i * VERTICAL_SPACING;
            layout.put(bubble, new Point(x, y));
        }

        double zoom = WorldMap.getZoom();

        for (Component bubble : staticBubbles){
            Point screenPos = worldMap.transform(1500, 480);

            int width = (int) (bubble.getPreferredSize().width *  zoom);
            int height = (int) (bubble.getPreferredSize().height * zoom);

            bubble.setBounds(screenPos.x + WorkSpaceScreen.SIDEBAR_WIDTH, screenPos.y, width, height);
        }

        return layout;
    }
}
