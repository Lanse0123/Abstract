package lanse.abstractt.core;

import lanse.abstractt.core.bubblesortlogic.FileListSort;
import lanse.abstractt.core.bubblesortlogic.SizeSort;
import lanse.abstractt.core.bubblesortlogic.TODOViewSort;

public class BubbleSorter {

    //no this will not use bubble sort to sort the bubbles in bubble sorter because bubble sorter sorts the bubbles and isn't the
    //algorithm name of bubble sort 💀💀💀

    //TODO - this class will be called after placing all the bubbles on screen.

    public enum Sorter{
        FILE_LIST_SORT, SIZE_SORT, TODO_VIEW_SORT, NOTHING
    }

    public static BubbleSorter.Sorter sorter = Sorter.FILE_LIST_SORT;

    public static void Sort(){

        switch (sorter) {
            case FILE_LIST_SORT : FileListSort.sort();
            case SIZE_SORT : SizeSort.sort();
            case TODO_VIEW_SORT : TODOViewSort.sort();

            case NOTHING : return;
        };
    }
}
