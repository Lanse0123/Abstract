package lanse.abstractt.storage;

import java.util.HashSet;
import java.util.Set;

public class ExcludedBubbleList {
    private static final Set<String> excludedFileEndings = new HashSet<>();

    static {
        excludedFileEndings.add("AbstractionVisualizerStorage"); // default
    }

    public static boolean isExcludedFile(String filePath) {
        for (String ending : excludedFileEndings) {
            if (filePath.contains(ending) || filePath.endsWith(ending)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> getExcludedEndings() {
        return new HashSet<>(excludedFileEndings);
    }

    public static void setExcludedEndings(Set<String> newList) {
        excludedFileEndings.clear();
        excludedFileEndings.addAll(newList);
        excludedFileEndings.add("AbstractionVisualizerStorage"); // THOU SHALT NEVER REMOVE THY DEFAULT
    }
}
