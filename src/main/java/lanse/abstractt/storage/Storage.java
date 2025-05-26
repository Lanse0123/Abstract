package lanse.abstractt.storage;

import lanse.abstractt.core.bubble.Bubble;

import java.util.*;

public class Storage {

    //TODO - eventually, add this to the project structure using Storage class
    private static final Map<Integer, List<Bubble>> depthToBubbles = new HashMap<>();
    public static Stack<String> selectedBubblePath;
    private static int currentDepth = 1;

    public static void addBubble(int depth, Bubble bubble) {
        //TODO - this will be used when the user wants to create a new bubble. Other than that, this is not needed.
        depthToBubbles.computeIfAbsent(depth, k -> new ArrayList<>()).add(bubble);
    }

    public static List<Bubble> getBubblesAtDepth(int depth) {
        //TODO - I want this to get all the JSON files at that depth, so like this includes folders, files, and functions.
        return depthToBubbles.getOrDefault(depth, Collections.emptyList());
    }

    public static List<Bubble> getBubblesAtCurrentDepth() {
        return depthToBubbles.getOrDefault(currentDepth, Collections.emptyList());
    }

    //TODO - this needs to also set the SelectedBubblePath to the right spot
    public static void setCurrentDepth(int depth) {
        assert (currentDepth >= depth);
        while (currentDepth > depth) {
            decreaseDepth();
        }
    }

    public static int getCurrentDepth() {
        return currentDepth;
    }

    public static int getDepth() {
        return currentDepth;
    }

    public static void increaseDepth(String nextPathPiece) {
        currentDepth++;
        selectedBubblePath.push(nextPathPiece);
    }

    public static void decreaseDepth() {
        currentDepth--;
        selectedBubblePath.pop();
    }

    //TODO - this will be used when opening a new project / workspace.
    public static void reset() {
        depthToBubbles.clear();
        currentDepth = 1;
        selectedBubblePath.clear();
    }
}
