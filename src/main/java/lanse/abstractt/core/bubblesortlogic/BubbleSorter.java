package lanse.abstractt.core.bubblesortlogic;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;

import java.awt.*;

public class BubbleSorter {

    //no this will not use bubble sort to sort the bubbles in bubble sorter because bubble sorter sorts the bubbles and isn't the
    //algorithm name of bubble sort 💀💀💀

    //TODO - this class will be called after placing all the bubbles on screen.

    public enum Sorter{
        FILE_LIST_SORT, SIZE_SORT, TODO_VIEW_SORT, NOTHING
    }
    public enum FunctionSorter{
        FILE_LIST_SORT, SIZE_SORT, TODO_VIEW_SORT, NOTHING //TODO - add other options or something
    }

    public static Sorter sorter = Sorter.NOTHING;
    public static FunctionSorter functionSorter = FunctionSorter.NOTHING;
    public static boolean isSorted = false;

    public static void sort(Bubble[] bubbles){
        if (isSorted) return;
        ProgressBarPanel.setLoading(true, "Sorting Bubbles");
        ProgressBarPanel.show();

        boolean isFunctionBubble = false;

        for (Bubble bubble : bubbles){
            if (bubble instanceof FunctionBubble){
                isFunctionBubble = true;
                break;
            }
        }

        switch (functionSorter){
            case FILE_LIST_SORT: FileListSort.sort(isFunctionBubble, bubbles);
            case SIZE_SORT: SizeSort.sort(isFunctionBubble, bubbles);
            case TODO_VIEW_SORT: TODOViewSort.sort(isFunctionBubble, bubbles);
            case NOTHING: break;
        }

        ProgressBarPanel.setProgress(1);
        ProgressBarPanel.hide();
        isSorted = true;
    }
}
