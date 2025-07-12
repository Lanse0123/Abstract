package lanse.abstractt.core.displaylogic;

import java.awt.*;

public class DisplayModeSelector {

    public enum DisplayMode {
        FILE_LIST, MAIN_LIST_DOWN, PROXIMITY_SIMULATOR, ANTI_COLLIDER, TODO_VIEW
    }

    public static DisplayMode displayMode = DisplayMode.FILE_LIST;

    public static int bubbleCount = 0;

    public static Point getNewBubblePosition(boolean isCodeBubble){

        if (!isCodeBubble) {
            bubbleCount += 1;

            return switch (displayMode) {
                case FILE_LIST -> FileList.getNewBubblePosition(bubbleCount);
                case MAIN_LIST_DOWN -> MainListDown.getNewBubblePosition(bubbleCount);
                case PROXIMITY_SIMULATOR -> ProximitySimulator.getNewBubblePosition(bubbleCount);
                case ANTI_COLLIDER -> AntiCollider.getNewBubblePosition(bubbleCount);
                case TODO_VIEW -> TodoView.getNewBubblePosition(bubbleCount);
            };
        } else {
            return new Point(1500, 480);
        }
    }

    public static void clearBubbles() {
        bubbleCount = 0;
    }
}
