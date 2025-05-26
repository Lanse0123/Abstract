package lanse.abstractt.core;

import lanse.abstractt.core.displaylogic.*;
import lanse.abstractt.storage.AbstractionBubbleStorage;

import java.awt.*;

public class DisplayModeSelector {

    public enum DisplayMode {
        FILE_LIST, MAIN_LIST_DOWN, PROXIMITY_SIMULATOR, ANTI_COLLIDER, TODO_VIEW
    }

    public static DisplayMode displayMode = DisplayMode.FILE_LIST;

    public static Point getNewBubblePosition(){

        //TODO - EW, this can be made with way less boilerplate (do this later)
        int bubbleCount = AbstractionBubbleStorage.getBubblesAtDepth(AbstractionBubbleStorage.getDepth()).size();

        return switch (displayMode) {
            case FILE_LIST -> FileList.getNewBubblePosition(bubbleCount);
            case MAIN_LIST_DOWN -> MainListDown.getNewBubblePosition(bubbleCount);
            case PROXIMITY_SIMULATOR -> ProximitySimulator.getNewBubblePosition(bubbleCount);
            case ANTI_COLLIDER -> AntiCollider.getNewBubblePosition(bubbleCount);
            case TODO_VIEW -> TodoView.getNewBubblePosition(bubbleCount);
        };
    }
}
