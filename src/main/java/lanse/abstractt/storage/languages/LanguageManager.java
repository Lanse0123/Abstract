package lanse.abstractt.storage.languages;

import lanse.abstractt.core.ColorPalette;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LanguageManager {

    public static Icon getIconFromPath(String path) {
        File file = new File(path);
        String extension;

        if (file.isDirectory()) {
            extension = "folder";
        } else {
            extension = getExtension(path);
        }

        String basePath = "/images/LanguageIcons/";

        if (extension.equals("folder")) {
            try {
                return new ImageIcon(LanguageManager.class.getResource(basePath + "DefaultDirectory.png"));
            } catch (Exception e) {
                try {
                    return new ImageIcon(LanguageManager.class.getResource(basePath + "DefaultFile.png"));
                } catch (Exception ignored) {}
            }
        }
        try {
            return new ImageIcon(LanguageManager.class.getResource(basePath + extension + ".png"));
        } catch (Exception e) {
            try {
                return new ImageIcon(LanguageManager.class.getResource(basePath + "DefaultFile.png"));
            } catch (Exception ignored) {}
        }
        return null;
    }

    //TODO - this should also work if the path is already an extension.
    public static String getExtension(String path) {
        String fileName = path.replace('\\', '/'); //idk if this works outside of windows
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        } else {
            return ""; // No extension found
        }
    }

    public static Color getLanguageColorFromPath(String path, boolean isTopBar) {
        String extension;

        // Allow either a full path or just the extension
        if (path.startsWith(".")) {
            extension = path.toLowerCase();
        } else {
            File file = new File(path);
            extension = file.isDirectory() ? "folder" : getExtension(path);
        }

        if (extension.startsWith(".")) extension = extension.replaceFirst(".", "");

        String basePath = "/LanguageDefinitions/" + extension + ".json";
        try {
            InputStream stream = LanguageManager.class.getResourceAsStream(basePath);
            if (stream == null){
                if (isTopBar) return Color.RED;
                return ColorPalette.ColorCategory.BUBBLES_AND_PROGRESS.getColor();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONObject obj = new JSONObject(json.toString());
            String hexColor = obj.getString("color");

            return Color.decode(hexColor);
        } catch (Exception e) {
            e.printStackTrace();
            return Color.BLACK;
        }
    }

    public static boolean isFileParsable(String path) {
        //This parsable check works correctly
        String extension;

        // Support both full file paths and raw extensions
        if (path.startsWith(".")) {
            extension = path.toLowerCase();
        } else {
            File file = new File(path);
            extension = file.isDirectory() ? "folder" : getExtension(path);
        }

        String jsonPath = "/LanguageDefinitions/" + extension + ".json";

        try (InputStream stream = LanguageManager.class.getResourceAsStream(jsonPath)) {
            if (stream == null) return false;

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONObject obj = new JSONObject(json.toString());
            return obj.optBoolean("parse", false); // returns false if key is missing
        } catch (Exception e) {
            e.printStackTrace(); // for debugging
            return false;
        }
    }

}
