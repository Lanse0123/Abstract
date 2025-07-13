package lanse.abstractt.storage;

import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.FunctionBubble;
import lanse.abstractt.core.bubble.TopBubble;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

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
            if (dir.isFile()){
                return 1;
            } else {
                return 0;
            }
        }
        return Arrays.stream(Objects.requireNonNull(dir.listFiles())).filter(f -> !f.getName().equals("AbstractionVisualizerStorage")).toArray().length;
    }

    public static void setCurrentDepth(int depth) {
        assert (currentDepth > depth + 1);
        while (currentDepth > depth + 1) {
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

    public static void saveAllBubbles(Container parent, boolean clearBubbles){
        for (Component comp : parent.getComponents()) {
            if (comp instanceof Bubble) {
                if (comp instanceof FunctionBubble functionBubble){
                    Storage.saveFunctionBubble(functionBubble);
                } else {
                    Storage.save((Bubble) comp);
                }
                if (clearBubbles){
                    parent.remove(comp);
                }
            }
        }
    }

    public static void addStructure(String filePath, String structure, String name, String desc, int start, Optional<Integer> end) {
        if (isAbstractionFile(filePath)) return;
        JSONObject json = loadOrCreateJson(filePath);
        json.put("compiled", true);
        JSONObject arr = json.optJSONObject(structure);
        if (arr == null) json.put(structure, arr = new JSONObject());

        JSONObject entry = new JSONObject();
        entry.put("desc", desc);
        entry.put("start", start);
        if (end.isPresent()) {
            entry.put("end", end.get());
        }
        arr.put(name, entry);

        writeJson(filePath, json);
    }

    public static void updateStructure(String filePath, String structure, String name, Optional<String> desc, Optional<Integer> start, Optional<Integer> end) {
        if (isAbstractionFile(filePath)) return;
        JSONObject json = loadOrCreateJson(filePath);
        JSONObject arr = json.optJSONObject(structure);
        if (arr == null) json.put(structure, arr = new JSONObject());

        System.out.println("Name: " + name);

        JSONObject entry = arr.optJSONObject(name);
        if (entry == null) arr.put(name, entry = new JSONObject());
        System.out.println("Entry: " + entry);
        if (start.isPresent()) {
            entry.put("start", start.get());
        }
        if (end.isPresent()) {
            entry.put("end", end.get());
        }
        if (desc.isPresent()) {
            entry.put("desc", desc.get());
        }
        System.out.println("About to write " + json);
        writeJson(filePath, json);
    }

    public static FunctionBubble[] loadFunctionBubbles(String filePath) {
        JSONObject json = loadJson(filePath);
        if (json == null) return new FunctionBubble[0];

        List<FunctionBubble> list = new ArrayList<>();
        for (String structure : json.keySet()) {
            if (isMetaKey(structure)) continue;
            JSONObject arr = json.optJSONObject(structure);
            if (arr == null) continue;

            for (String name : arr.keySet()) {
                JSONObject obj = arr.getJSONObject(name);
                String desc  = obj.optString("desc", "");
                int startLine  = obj.getInt("start");
                Optional<Integer> endLine;
                try {
                    endLine = Optional.of(obj.getInt("end"));
                } catch (JSONException e) {
                    endLine = Optional.empty();
                }
                list.add(new FunctionBubble(name, desc, filePath, startLine, endLine, structure,false));
            }
        }
        return list.toArray(new FunctionBubble[0]);
    }

    public static void saveFunctionBubble(FunctionBubble fb) {
        updateStructure(fb.getFilePath(), fb.getStructureType(), fb.getTitle(), Optional.ofNullable(fb.getDescription()), Optional.of(fb.getStartLineNumber()), fb.getEndLineNumber());
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
        System.out.println("Starting to write JSON to " + filePath);
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
