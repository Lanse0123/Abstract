package lanse.abstractt.core.displaylogic;

import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.Bubble;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class DisplayModeSelector {

    public enum DisplayMode {
        FILE_LIST, MAIN_LIST_DOWN, PROXIMITY_SIMULATOR, ANTI_COLLIDER, TODO_VIEW, GOURCE_MAP, CLUSTERPLOT_MAP
    }

    public static List<DisplayMode> staticMapList = List.of(
            DisplayMode.GOURCE_MAP
    );

    public static DisplayMode displayMode = DisplayMode.FILE_LIST;

    public static Map<Bubble, Point> getBubbleLayout(Bubble[] bubbles, Component[] staticBubbles, WorldMap worldMap) {

        return switch (displayMode) {
            case FILE_LIST -> FileList.getLayout(bubbles, staticBubbles, worldMap);
            case MAIN_LIST_DOWN -> MainListDown.getLayout(bubbles, staticBubbles);
            case PROXIMITY_SIMULATOR -> ProximitySimulator.getLayout(bubbles, staticBubbles);
            case ANTI_COLLIDER -> AntiCollider.getLayout(bubbles, staticBubbles);
            case TODO_VIEW -> TodoView.getLayout(bubbles, staticBubbles);

            //static maps below here
            case GOURCE_MAP -> GourceMap.getLayout(bubbles, staticBubbles, worldMap);
            case CLUSTERPLOT_MAP -> ClusterPlotMap.getLayout(bubbles, staticBubbles, worldMap);
        };
    }

    public static void clearStaticMaps() {
        GourceMap.clear();
        ClusterPlotMap.clear();
    }
}
