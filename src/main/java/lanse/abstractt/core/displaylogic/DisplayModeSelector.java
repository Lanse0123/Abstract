package lanse.abstractt.core.displaylogic;

import lanse.abstractt.core.bubble.Bubble;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DisplayModeSelector {

    public enum DisplayMode {
        FILE_LIST, MAIN_LIST_DOWN, PROXIMITY_SIMULATOR, ANTI_COLLIDER, TODO_VIEW, GOURCE_MAP
    }

    //TODO - These will be the maps that are generated at runtime instead of
    // from users clicking bubbles. (dont do this yet)
    public static List staticMapList = List.of(new List[]{
            Collections.singletonList(DisplayMode.GOURCE_MAP)
    });

    public static DisplayMode displayMode = DisplayMode.FILE_LIST;

    public static Map<Bubble, Point> getBubbleLayout(Bubble[] bubbles) {
        return switch (displayMode) {
            case FILE_LIST -> FileList.getLayout(bubbles);
            case MAIN_LIST_DOWN -> MainListDown.getLayout(bubbles);
            case PROXIMITY_SIMULATOR -> ProximitySimulator.getLayout(bubbles);
            case ANTI_COLLIDER -> AntiCollider.getLayout(bubbles);
            case TODO_VIEW -> TodoView.getLayout(bubbles);

            case GOURCE_MAP -> Collections.emptyMap(); // no layout yet
        };
    }

}
