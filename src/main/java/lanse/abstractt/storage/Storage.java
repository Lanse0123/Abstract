package lanse.abstractt.storage;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.TopBubble;
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

    public static List<Bubble> getBubblesAtCurDepth() {
        if (currentDepth == 1) {
            return List.of(Settings.topBubble);
        }
        //TODO - I want this to get all the JSON files at that depth, so like this includes folders, files, and functions.
        File dir = new File(selectedBubblePath.peek());
        if (!dir.isDirectory()) {
            return List.of(load(dir.getPath(), false));
        }
        List<Bubble> bubbles = new ArrayList<>();
        for (File f : Objects.requireNonNull(dir.listFiles())){
            if (f.getName().equals("AbstractionVisualizerStorage")) {
                continue;
            }
            bubbles.add(load(f.getPath(), false));
        }
        return bubbles;
    }

    public static int getNumBubblesAtDepth(int depth) {
        if (depth == 0) {
            return 1;
        }
        File dir = new File(selectedBubblePath.elementAt(depth));
        if (!dir.isDirectory()) {
            return 0;
        }
        return Arrays.stream(dir.listFiles()).filter(f -> !f.getName().equals("AbstractionVisualizerStorage")).toArray().length;
    }

    public static int getNumBubblesAtCurrentDepth() {
        return getNumBubblesAtDepth(currentDepth-1);
    }

    public static void setCurrentDepth(int depth) {
        assert (currentDepth > depth+1);
        while (currentDepth > depth+1) {
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
        TopBubble.languageMap.clear();
        TopBubble.languagePercents.clear();
        TopBubble.languageColors.clear();
    }

    // Load settings from JSON
    //TODO: make this list into a class containing path, name and description
    public static Bubble load(String filePath, boolean loadFunctionBubbles) {
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
            String description = json.optString("desc", "null");

            return new Bubble(name, description, path);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            // TODO: throw an exception or just create defaults?
            System.out.println("Unable to load info for file: " + filePath);
            System.out.println("Error reading: " + e);
            e.printStackTrace();
            return new Bubble("", "", "");
        }
    }

    // Save settings to disk
    public static void save(Bubble bubble) {
        if (bubble.getFilePath().contains("AbstractionVisualizerStorage")) return;
        try {
            Files.createDirectories(Path.of(mapToAbstractionPath(bubble.getFilePath(), false)).getParent());

            JSONObject json = new JSONObject();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            json.put("path", bubble.getFilePath());
            json.put("name", bubble.getTitle());
            json.put("desc", bubble.getDescription());

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            try (FileWriter file = new FileWriter(mapToAbstractionPath(bubble.getFilePath(), false))) {
                file.write(json.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addStructure(String filepath, String functionName, Integer startIndex) {
        if (filepath.contains("AbstractionVisualizerStorage")) return;
        try {
            Files.createDirectories(Path.of(mapToAbstractionPath(filepath, false)).getParent());

            JSONObject json = new JSONObject();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //TODO - add a new json thing for each structural point the same way save does it.
            // Like there should be imports, fields, classes, and functions.
            // Make sure they are all named uniquely so theres no overlap.

            //TODO - then make sure to add some json flag that tells it that its already compiled, so LSP and AI dont
            // need to go over the same thing many times

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            try (FileWriter file = new FileWriter(mapToAbstractionPath(filepath, false))) {
                file.write(json.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String mapToAbstractionPath(String filePath, boolean wantDir){
        String localPath = (String) filePath.subSequence(selectedBubblePath.firstElement().length(), filePath.length());
        return selectedBubblePath.firstElement() + "/AbstractionVisualizerStorage/" + localPath + (wantDir ? "" : ".json");
    }
}
