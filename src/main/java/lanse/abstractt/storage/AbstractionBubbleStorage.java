package lanse.abstractt.storage;

import lanse.abstractt.core.bubble.Bubble;

import java.util.*;

public class AbstractionBubbleStorage {

    //TODO - eventually, add this to the project structure using Storage class
    private static final Map<Integer, List<Bubble>> depthToBubbles = new HashMap<>();
    private static int currentDepth = 1;

    public static void addBubble(int depth, Bubble bubble) {
        depthToBubbles.computeIfAbsent(depth, k -> new ArrayList<>()).add(bubble);
    }

    public static List<Bubble> getBubblesAtDepth(int depth) {
        return depthToBubbles.getOrDefault(depth, Collections.emptyList());
    }

    public static void clearCurrentDepth() {
        depthToBubbles.remove(currentDepth);
    }

    public static void setCurrentDepth(int depth) {
        currentDepth = depth;
    }

    public static int getCurrentDepth() {
        return currentDepth;
    }

    public static int getDepth() {
        return currentDepth;
    }

    public static void increaseDepth() {
        currentDepth++;
    }

    public static void setDepth(int d) {
        currentDepth = d;
    }

    public static void reset() {
        depthToBubbles.clear();
        currentDepth = 1;
    }
}
