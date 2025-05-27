package lanse.abstractt.storage;

import lanse.abstractt.core.bubble.Bubble;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    // Load settings from JSON
    //TODO: make this list into a class containing path, name and description
    public static String[] load(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(mapToAbstractionPath(filePath)))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject json = new JSONObject(jsonContent.toString());
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            String path = json.optString("path", "null");
            String name = json.optString("name", "null");
            String description = json.optString("description", "null");

            return new String[]{path, name, description};

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            // TODO: throw an exception or just create defaults?
            System.out.println("Unable to load info for file: " + e);
            return new String[]{};
        }
    }

    // Save settings to disk
    public static void save(Bubble bubble) {
        try {
            if (!Files.exists(Path.of(bubble.getFilePath()))) {
                Files.createDirectories(Path.of(mapToAbstractionPath(bubble.getFilePath())));
            }

            JSONObject json = new JSONObject();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            json.put("path", bubble.getFilePath());
            json.put("name", bubble.getName());
            json.put("description", bubble.getDescription());

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            try (FileWriter file = new FileWriter(mapToAbstractionPath(bubble.getFilePath()))) {
                file.write(json.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String mapToAbstractionPath(String filePath){
        String localPath = (String) filePath.subSequence(selectedBubblePath.firstElement().length(), filePath.length());
        return selectedBubblePath.firstElement() + "/AbstractionVisualizerStorage" + localPath + ".json";
    }
}
