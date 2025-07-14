package lanse.abstractt.core.displaylogic;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class DisplayModeSelector {

    public enum DisplayMode {
        FILE_LIST, MAIN_LIST_DOWN, PROXIMITY_SIMULATOR, ANTI_COLLIDER, TODO_VIEW, GOURCE_MAP
    }

    //TODO - for now this auto corrected BS works. These will be the maps that are generated at runtime instead of
    // from users clicking bubbles.
    public static List staticMapList = List.of(new List[]{
            Collections.singletonList(DisplayMode.GOURCE_MAP)
    });

    public static DisplayMode displayMode = DisplayMode.FILE_LIST;

    public static int bubbleCount = 0;

    public static Point getNewBubblePosition(boolean isCodeBubble){

        if (isCodeBubble) {
            return new Point(1500, 480);
        }

        bubbleCount += 1;

        return switch (displayMode) {
            case FILE_LIST -> FileList.getNewBubblePosition(bubbleCount);
            case MAIN_LIST_DOWN -> MainListDown.getNewBubblePosition(bubbleCount);
            case PROXIMITY_SIMULATOR -> ProximitySimulator.getNewBubblePosition(bubbleCount);
            case ANTI_COLLIDER -> AntiCollider.getNewBubblePosition(bubbleCount);
            case TODO_VIEW -> TodoView.getNewBubblePosition(bubbleCount);

            case GOURCE_MAP -> null;
        };

    }

    public static void clearBubbles() {
        bubbleCount = 0;
    }
}
