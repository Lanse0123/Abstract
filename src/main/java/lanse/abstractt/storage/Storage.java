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
    public static void load(String filePath) {
        //TODO - this needs to be mapped to the Abstraction Storage thing
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            // No settings file yet? Just use defaults.
            System.out.println("No settings file found. Using default settings.");
        }
    }

    // Save settings to disk
    public static void save(String filePath, String name, String description) {
        try {
            if (!Files.exists(Path.of(filePath))) {
                //TODO - This one should be mapped to the AbstractVisualizerStorage thing
                Files.createDirectories(Path.of(filePath));
            }

            JSONObject json = new JSONObject();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            json.put("path", filePath);
            json.put("name", name);
            json.put("description", description);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //TODO - this should also be mapped to the Abstract Storage
            try (FileWriter file = new FileWriter(filePath)) {
                file.write(json.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String mapToAbstractionPath(String filePath){

        return "egg";
    }
}
