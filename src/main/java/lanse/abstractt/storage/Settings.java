package lanse.abstractt.storage;

import dev.dirs.ProjectDirectories;
import lanse.abstractt.core.bubblesortlogic.BubbleSorter;
import lanse.abstractt.core.displaylogic.DisplayModeSelector;
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

    private static List<String> recentProjects = new ArrayList<>();

    // Load settings from JSON
    public static void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_PATH))) {

            // added this line to show where to find settings file
            System.out.println("Settings file found at " + SETTINGS_PATH + " .");

            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject json = new JSONObject(jsonContent.toString());
            //TODO - remember to also add new things to SettingsScreen class when adding them here.
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

//            volume = json.optInt("volume", 100);
//            background = json.optString("background", "Warped Plane");
//            showDeathMarkers = json.optBoolean("showDeathMarkers", true);
//            playIllegalMoveSound = json.optBoolean("playIllegalMoveSound", false);

            DisplayModeSelector.displayMode = DisplayModeSelector.DisplayMode.valueOf(
                    json.optString("displayMode", DisplayModeSelector.DisplayMode.FILE_LIST.toString()));

            BubbleSorter.sorter = BubbleSorter.Sorter.valueOf(
                    json.optString("sortMode", BubbleSorter.Sorter.FILE_LIST_SORT.toString()));
            BubbleSorter.functionSorter = BubbleSorter.FunctionSorter.valueOf(
                    json.optString("functionSortMode", BubbleSorter.FunctionSorter.FILE_LIST_SORT.toString()));

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

            //provides clearer messages
            File settingsFile = new File(SETTINGS_PATH);
            if(!settingsFile.exists())
                System.out.print("No settings file found at " + SETTINGS_PATH + " . ");
            else if (settingsFile.isDirectory())
                //this error message can happen, since "settings.json" is a valid directory name on linux 
                System.out.print("File found at settings path ( " + SETTINGS_PATH + " ) but is a directory. ");
            else
                System.out.print("Error while reading settings file ( " + SETTINGS_PATH + " ). ");

            // No settings file yet? Just use defaults.
            System.out.println("Using default settings.");
        }
    }

    // Save settings to disk. returns true if settings could be saved, otherwise returns false
    public static boolean save() {
        try {
            if (!Files.exists(SETTINGS_DIR)) {
                Files.createDirectories(SETTINGS_DIR);
            }

            JSONObject json = new JSONObject();
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////

//            json.put("volume", volume);
//            json.put("background", background);
//            json.put("showDeathMarkers", showDeathMarkers);
//            json.put("playIllegalMoveSound", playIllegalMoveSound);

            json.put("displayMode", DisplayModeSelector.displayMode.name());

            json.put("sortMode", BubbleSorter.sorter.name());
            json.put("functionSortMode", BubbleSorter.functionSorter.name());

            json.put("recentProjects", recentProjects);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            try (FileWriter file = new FileWriter(SETTINGS_PATH)) {
                file.write(json.toString(4));
            }

            //validates that settings were saved
            try (BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_PATH))) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }

                JSONObject savedJson = new JSONObject(jsonContent.toString());

                return savedJson.toString(4).equals(json.toString(4));

            } catch (IOException e) {
                System.out.println("Unable to save settings ( " + SETTINGS_PATH + " ).");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void returnToDefaultSettings() {
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        volume = 100;
//        background = "Warped Plane";
//        showDeathMarkers = true;
//        playIllegalMoveSound = false;

        DisplayModeSelector.displayMode = DisplayModeSelector.DisplayMode.FILE_LIST;

        BubbleSorter.sorter = BubbleSorter.Sorter.FILE_LIST_SORT;
        BubbleSorter.functionSorter = BubbleSorter.FunctionSorter.FILE_LIST_SORT;

        // recentProjects not cleared
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        save();
    }

    public static List<String> getRecentProjects() { return new ArrayList<>(recentProjects); }

    public static void addRecentProject(String path) {
        recentProjects.remove(path);
        recentProjects.add(0, path);
        if (recentProjects.size() > MAX_RECENT_PROJECTS) {
            recentProjects = recentProjects.subList(0, MAX_RECENT_PROJECTS);
        }
    }
}
