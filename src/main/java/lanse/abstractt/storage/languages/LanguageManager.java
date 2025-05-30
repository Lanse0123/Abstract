package lanse.abstractt.storage.languages;

import javax.swing.*;
import java.awt.*;

public class LanguageManager {

    public static Image getImageFromLanguage(String extension) {
        extension = extension.toLowerCase();
        String basePath = "/resources/images/LanguageIcons/";

        if (extension.equals("folder")) {
            try {
                return new ImageIcon(LanguageManager.class.getResource(basePath + "DefaultDirectory.png")).getImage();
            } catch (Exception e) {
                try {
                    return new ImageIcon(LanguageManager.class.getResource(basePath + "DefaultFile.png")).getImage();
                } catch (Exception ignored) {}
            }
        }
        try {
            return new ImageIcon(LanguageManager.class.getResource(basePath + extension + ".png")).getImage();
        } catch (Exception e) {
            try {
                return new ImageIcon(LanguageManager.class.getResource(basePath + "DefaultFile.png")).getImage();
            } catch (Exception ignored) {}
        }
        return null;
    }
}
