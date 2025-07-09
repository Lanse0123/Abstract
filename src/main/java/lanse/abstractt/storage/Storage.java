package lanse.abstractt.storage;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.bubble.TopBubble;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            return List.of(load(dir.getPath()));
        }
        List<Bubble> bubbles = new ArrayList<>();
        for (File f : Objects.requireNonNull(dir.listFiles())){
            if (f.getName().equals("AbstractionVisualizerStorage")) {
                continue;
            }
            bubbles.add(load(f.getPath()));
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
        return Arrays.stream(Objects.requireNonNull(dir.listFiles())).filter(f -> !f.getName().equals("AbstractionVisualizerStorage")).toArray().length;
    }

    public static int getNumBubblesAtCurrentDepth() {
        return getNumBubblesAtDepth(currentDepth - 1);
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

    public static Bubble load(String filePath) {
        JSONObject json = loadJson(filePath);
        if (json == null) return new Bubble("", "", "", false);

        String path = json.optString("path", "");
        String name = json.optString("name", "");
        String description = json.optString("desc", "");
        return new Bubble(name, description, path, true);
    }

    public static void save(Bubble bubble) {
        if (isAbstractionFile(bubble.getFilePath())) return;
        JSONObject json = loadOrCreateJson(bubble.getFilePath());
        json.put("path", bubble.getFilePath());
        json.put("name", bubble.getTitle());
        json.put("desc", bubble.getDescription());
        writeJson(bubble.getFilePath(), json);
    }

    public static void addStructure(String filePath, String structure, String name, String desc, int line) {
        if (isAbstractionFile(filePath)) return;
        JSONObject json = loadOrCreateJson(filePath);
        json.put("compiled", true);
        JSONArray arr = json.optJSONArray(structure);
        if (arr == null) json.put(structure, arr = new JSONArray());

        JSONObject entry = new JSONObject();
        entry.put("name", name);
        entry.put("desc", desc);
        entry.put("line", line);
        arr.put(entry);

        writeJson(filePath, json);
    }

    public static FunctionBubble[] loadFunctionBubbles(String filePath) {
        JSONObject json = loadJson(filePath);
        if (json == null) return new FunctionBubble[0];

        List<FunctionBubble> list = new ArrayList<>();
        for (String structure : json.keySet()) {
            if (isMetaKey(structure)) continue;
            JSONArray arr = json.optJSONArray(structure);
            if (arr == null) continue;

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String name  = obj.optString("name", structure + i);
                String desc  = obj.optString("desc", "");
                int line  = obj.optInt("line", -1);
                list.add(new FunctionBubble(name, desc, filePath, line, structure,false));
            }
        }
        return list.toArray(new FunctionBubble[0]);
    }

    public static void saveFunctionBubble(FunctionBubble fb) {
        addStructure(fb.getFilePath(), fb.getStructureType(), fb.getTitle(), fb.getDescription(), fb.getLineNumber());
    }


    // helper functions below


    private static boolean isAbstractionFile(String filePath) {
        return filePath.contains("AbstractionVisualizerStorage");
    }

    private static boolean isMetaKey(String key) {
        return Set.of("path","name","desc","compiled").contains(key);
    }

    private static JSONObject loadJson(String filePath) {
        Path p = abstractionPath(filePath);
        if (!Files.exists(p)) return null;
        try {
            String txt = Files.readString(p);
            return new JSONObject(txt);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject loadOrCreateJson(String filePath) {
        JSONObject json = loadJson(filePath);
        return (json != null) ? json : new JSONObject();
    }

    private static void writeJson(String filePath, JSONObject json) {
        Path path = abstractionPath(filePath);
        try {
            Files.createDirectories(path.getParent());
            try (FileWriter w = new FileWriter(path.toFile())) {
                w.write(json.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path abstractionPath(String filePath) {
        String local = filePath.substring(selectedBubblePath.firstElement().length());
        return Paths.get(selectedBubblePath.firstElement(), "AbstractionVisualizerStorage", local + ".json");
    }
}
