package lanse.abstractt.storage.languages;

import javax.swing.*;
import java.io.File;

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
}
