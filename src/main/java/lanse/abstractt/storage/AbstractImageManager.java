package lanse.abstractt.storage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.Objects;

public class AbstractImageManager {

    private static ImageIcon getIcon(String resource_path) {
        try {
            return new ImageIcon(ImageIO.read(Objects.requireNonNull(AbstractImageManager.class.getResource(resource_path))));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load image resource", e);
        }
    }

    public static Icon getEmptyIcon() {
        return getIcon("/images/abstract/Empty Pixel.png");
    }

    public static Icon getEditIcon() {
        return getIcon("/images/abstract/Green Edit Button.png");
    }

    public static Icon getCheckMarkIcon() {
        return getIcon("/images/abstract/Check Mark Button.png");
    }

    public static Icon getCancelIcon() {
        return getIcon("/images/abstract/Red Undo Button.png");
    }

    public static ImageIcon getMainMenuBackground() {
        return getIcon("/images/abstract/Abstract MainMenu Background.png");
    }

    public static ImageIcon getLogoIcon() {
        return getIcon("/images/abstract/Abstract Logo Icon small.png");
    }

    public static ImageIcon getMainTitleIcon(){
        return getIcon("/images/abstract/Abstract Logo 1.png");
    }

    public static ImageIcon getCreditsBackground() {
        return getIcon("/images/abstract/Abstract Credits Background.png");
    }
}
