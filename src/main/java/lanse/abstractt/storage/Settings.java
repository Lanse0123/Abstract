package lanse.abstractt.storage;

import dev.dirs.ProjectDirectories;
import lanse.abstractt.core.DisplayModeSelector;
import lanse.abstractt.core.bubble.TopBubble;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Settings {

    private static final Path SETTINGS_DIR = Paths.get(ProjectDirectories.from("dev", "Lanse", "Abstract").configDir);
    private static final String SETTINGS_PATH = SETTINGS_DIR + "/settings.json";
    public static String selectedProjectPath = null;
    public static TopBubble topBubble;
    private static final int MAX_RECENT_PROJECTS = 10;

    //TODO - phase out the World War Chess settings eventually like sound, background, death markers, and illegal sounds.
    private static int volume = 100;
    private static String background = "Warped Plane";
    private static boolean showDeathMarkers = true;
    private static boolean playIllegalMoveSound = false;
    private static List<String> recentProjects = new ArrayList<>();

    // Load settings from JSON
    public static void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_PATH))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject json = new JSONObject(jsonContent.toString());
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            volume = json.optInt("volume", 100);
            background = json.optString("background", "Warped Plane");
            showDeathMarkers = json.optBoolean("showDeathMarkers", true);
            playIllegalMoveSound = json.optBoolean("playIllegalMoveSound", false);

            DisplayModeSelector.displayMode = DisplayModeSelector.DisplayMode.valueOf(
                    json.optString("displayMode", DisplayModeSelector.DisplayMode.FILE_LIST.toString()));

            if (json.has("recentProjects")) {
                recentProjects.clear();
                for (Object obj : json.getJSONArray("recentProjects")) {
                    if (obj instanceof String pathStr) {
                        recentProjects.add(pathStr);
                    }
                }
            }

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            // No settings file yet? Just use defaults.
            System.out.println("No settings file found. Using default settings.");
        }
    }

    // Save settings to disk
    public static void save() {
        try {
            if (!Files.exists(SETTINGS_DIR)) {
                Files.createDirectories(SETTINGS_DIR);
            }

            JSONObject json = new JSONObject();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

            json.put("volume", volume);
            json.put("background", background);
            json.put("showDeathMarkers", showDeathMarkers);
            json.put("playIllegalMoveSound", playIllegalMoveSound);

            json.put("displayMode", DisplayModeSelector.displayMode.name());

            json.put("recentProjects", recentProjects);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            try (FileWriter file = new FileWriter(SETTINGS_PATH)) {
                file.write(json.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void returnToDefaultSettings() {
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        volume = 100;
        background = "Warped Plane";
        showDeathMarkers = true;
        playIllegalMoveSound = false;

        DisplayModeSelector.displayMode = DisplayModeSelector.DisplayMode.FILE_LIST;

        // recentProjects not cleared
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        save();
    }

    public static List<String> getRecentProjects() { return new ArrayList<>(recentProjects); }
    public static int getVolume() { return volume; }
    public static void setVolume(int v) { volume = v; }

    public static String getBackground() { return background; }
    public static void setBackground(String bg) { background = bg; }

    public static boolean isShowDeathMarkers() { return showDeathMarkers; }
    public static void setShowDeathMarkers(boolean show) { showDeathMarkers = show; }

    public static boolean isPlayIllegalMoveSound() { return playIllegalMoveSound; }
    public static void setPlayIllegalMoveSound(boolean play) { playIllegalMoveSound = play; }
    public static void addRecentProject(String path) {
        recentProjects.remove(path);
        recentProjects.add(0, path);
        if (recentProjects.size() > MAX_RECENT_PROJECTS) {
            recentProjects = recentProjects.subList(0, MAX_RECENT_PROJECTS);
        }
    }
}
