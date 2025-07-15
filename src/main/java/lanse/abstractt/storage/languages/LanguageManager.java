package lanse.abstractt.storage.languages;

import lanse.abstractt.core.ColorPalette;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;
import java.util.Optional;

public class LanguageManager {

    private static final String ICON_BASE_PATH = "/images/languageicons/";
    private static final String DEFINITION_BASE_PATH = "/languagedefinitions/";

    public static final Color UNKNOWN_FILE_COLOR = Color.WHITE;

    public static Icon getIconFromPath(String path) {
        String ext = resolveExtension(path);
        if (ext.equals("folder")) ext = "DefaultDirectory";
        return loadIcon(ext).or(() -> loadIcon("DefaultFile")).orElse(null);
    }

    public static Color getLanguageColorFromPath(String path, boolean isTopBar) {
        String ext = resolveExtension(path);
        JSONObject def = loadLanguageDefinition(ext);
        if (def != null && def.has("color")) {
            try {
                return Color.decode(def.getString("color"));
            } catch (Exception ignored) {}
        }
        return isTopBar ? UNKNOWN_FILE_COLOR : ColorPalette.ColorCategory.BUBBLES_AND_PROGRESS.getColor();
    }

    public static boolean isFileParsable(String path) {
        JSONObject def = loadLanguageDefinition(resolveExtension(path));
        return def != null && def.optBoolean("parse", false);
    }

    public static String getLanguageName(String path) {
        JSONObject def = loadLanguageDefinition(resolveExtension(path));
        return def != null ? def.optString("language", "false") : "false";
    }

    public static String languageHasLSP(String path) {
        JSONObject def = loadLanguageDefinition(resolveExtension(path));
        return def != null ? def.optString("lsp", "false") : "false";
    }

    public static String getExtension(String path) {
        String fileName = path.replace('\\', '/');
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0 && dotIndex < fileName.length() - 1) ? fileName.substring(dotIndex + 1).toLowerCase() : "";
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private static String resolveExtension(String path) {
        if (path.startsWith(".")) return path.substring(1).toLowerCase();
        File file = new File(path);
        return file.isDirectory() ? "folder" : getExtension(path);
    }

    private static Optional<Icon> loadIcon(String name) {
        try {
            return Optional.of(new ImageIcon(Objects.requireNonNull(LanguageManager.class.getResource(ICON_BASE_PATH + name + ".png"))));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static JSONObject loadLanguageDefinition(String ext) {
        try (InputStream stream = LanguageManager.class.getResourceAsStream(DEFINITION_BASE_PATH + ext + ".json")) {
            if (stream == null) return null;

            StringBuilder json = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
            }
            return new JSONObject(json.toString());
        } catch (Exception e) {
            return null;
        }
    }
}