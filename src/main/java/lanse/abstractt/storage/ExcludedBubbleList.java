package lanse.abstractt.storage;

public class ExcludedBubbleList {

    public static boolean isExcludedFile(String filePath) {
        if (filePath.contains("AbstractionVisualizerStorage")) {
            return true;
        }

        //TODO - check excluded list here before returning false.

        return false;
    }

    //TODO - this will store the excluded file types the user specifies. For example, they could block or ignore all exe files
    // or block / ignore all build files, or stuff like that. I also might move AbstractVisualizerStorage here as a default one
    // that cant be removed. This can be stored and saved in settings.
}