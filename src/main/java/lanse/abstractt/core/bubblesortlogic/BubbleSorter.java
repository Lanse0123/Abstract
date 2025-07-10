package lanse.abstractt.core.bubblesortlogic;

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

    public static Sorter sorter = Sorter.FILE_LIST_SORT;
    public static FunctionSorter functionSorter = FunctionSorter.FILE_LIST_SORT;

    public static void Sort(boolean isFunctionBubble){

        if (!isFunctionBubble) {
            switch (sorter) {
                case FILE_LIST_SORT: FileListSort.sort(false);
                case SIZE_SORT: SizeSort.sort(false);
                case TODO_VIEW_SORT: TODOViewSort.sort(false);
                case NOTHING: return;
            }
        } else {
            switch (functionSorter){
                case FILE_LIST_SORT: FileListSort.sort(true);
                case SIZE_SORT: SizeSort.sort(true);
                case TODO_VIEW_SORT: TODOViewSort.sort(true);
                case NOTHING: return;
            }
        }
    }
}
