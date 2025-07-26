package lanse.abstractt.core.bubblesortlogic;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;

import java.awt.*;
import java.util.Map;


public class BubbleSorter {

    //no this will not use bubble sort to sort the bubbles in bubble sorter because bubble sorter sorts the bubbles and isn't the
    //algorithm name of bubble sort 💀💀💀


    public enum Sorter{
        FILE_LIST_SORT, SIZE_SORT, TODO_VIEW_SORT, NOTHING, SIZE_SORT_StL, SIZE_SORT_LtS
    }
    public enum FunctionSorter{
        FILE_LIST_SORT, SIZE_SORT, TODO_VIEW_SORT, NOTHING, SIZE_SORT_StL, SIZE_SORT_LtS //TODO - add other options or something
    }

    public static Sorter sorter = Sorter.NOTHING;
    public static FunctionSorter functionSorter = FunctionSorter.NOTHING;

    public static Map<Bubble, Point> layoutOverride = null;
    public static boolean isSorted = false;

    public static void sort(Bubble[] bubbles){
        if (isSorted) return;

        layoutOverride = null;

        ProgressBarPanel.setLoading(true, "Sorting Bubbles");
        ProgressBarPanel.show();

        boolean isFunctionBubble = false;
        for (Bubble bubble : bubbles){
            if (bubble instanceof FunctionBubble){
                isFunctionBubble = true;
                break;
            }
        }
        if (isFunctionBubble) {
            switch (functionSorter) {
                case FILE_LIST_SORT -> FileListSort.sort(isFunctionBubble, bubbles);
                case SIZE_SORT -> layoutOverride = SizeSort.sort(isFunctionBubble, bubbles, null);
                case SIZE_SORT_LtS -> layoutOverride = SizeSort.sort(isFunctionBubble, bubbles, "lts");
                case SIZE_SORT_StL -> layoutOverride = SizeSort.sort(isFunctionBubble, bubbles, "stl");
                case TODO_VIEW_SORT -> TODOViewSort.sort(isFunctionBubble, bubbles);
                case NOTHING -> { break; }
            }
        } else {
            switch (sorter) {
                case FILE_LIST_SORT -> FileListSort.sort(isFunctionBubble, bubbles);
                case SIZE_SORT -> layoutOverride = SizeSort.sort(isFunctionBubble, bubbles, null);
                case SIZE_SORT_LtS -> layoutOverride = SizeSort.sort(isFunctionBubble, bubbles, "lts");
                case SIZE_SORT_StL -> layoutOverride = SizeSort.sort(isFunctionBubble, bubbles, "stl");
                case TODO_VIEW_SORT -> layoutOverride = TODOViewSort.sort(isFunctionBubble, bubbles);
                case NOTHING -> { break; }
            }
        }

        ProgressBarPanel.setProgress(1);
        ProgressBarPanel.hide();
        isSorted = true;
    }
}
