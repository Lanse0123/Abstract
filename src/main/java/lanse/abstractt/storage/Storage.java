package lanse.abstractt.storage;

import lanse.abstractt.core.bubble.Bubble;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Storage {

    //TODO - eventually, add this to the project structure using Storage class
//    private static final Map<Integer, List<Bubble>> depthToBubbles = new HashMap<>();
    public static Stack<String> selectedBubblePath;
    private static int currentDepth = 1;

    public static List<Bubble> getBubblesAtDepth(int depth) {
        //TODO - I want this to get all the JSON files at that depth, so like this includes folders, files, and functions.
        File dir = new File(mapToAbstractionPath(selectedBubblePath.elementAt(depth), true));
        List<Bubble> bubbles = new ArrayList<>();
        for (File f : Objects.requireNonNull(dir.listFiles())){
            bubbles.add(load(f.getPath()));
        }
        return bubbles;
    }

    public static int getNumBubblesAtDepth(int depth) {
        File dir = new File(mapToAbstractionPath(selectedBubblePath.elementAt(depth), true));
        return Objects.requireNonNull(dir.listFiles()).length;
    }

    public static int getNumBubblesAtCurrentDepth() {
        return getNumBubblesAtDepth(currentDepth-1);
    }

    public static void setCurrentDepth(int depth) {
        assert (currentDepth > depth);
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
//        depthToBubbles.clear();
        currentDepth = 1;
        selectedBubblePath.clear();
    }

    // Load settings from JSON
    //TODO: make this list into a class containing path, name and description
    public static Bubble load(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(mapToAbstractionPath(filePath, false)))) {
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

            return new Bubble(name, description, path);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            // TODO: throw an exception or just create defaults?
            System.out.println("Unable to load info for file: " + filePath);
            System.out.println("Error reading: " + e);
            return new Bubble("", "", "");
        }
    }

    // Save settings to disk
    public static void save(Bubble bubble) {
        if (bubble.getFilePath().contains("AbstractionVisualizerStorage")) return;
        try {
            if (!Files.exists(Path.of(bubble.getFilePath()))) {
                Files.createDirectories(Path.of(mapToAbstractionPath(bubble.getFilePath(), false)));
            }

            JSONObject json = new JSONObject();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            json.put("path", bubble.getFilePath());
            json.put("name", bubble.getName());
            json.put("description", bubble.getDescription());

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            try (FileWriter file = new FileWriter(mapToAbstractionPath(bubble.getFilePath(), false))) {
                file.write(json.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String mapToAbstractionPath(String filePath, boolean wantDir){
        System.out.println("Mapping " + filePath);
        String localPath = (String) filePath.subSequence(selectedBubblePath.firstElement().length(), filePath.length());
        System.out.println("To " + localPath);
        return selectedBubblePath.firstElement() + "/AbstractionVisualizerStorage/" + localPath + (wantDir ? "" : ".json");
    }
}
