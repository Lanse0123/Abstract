package lanse.abstractt.storage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class AbstractImageManager {

    public static ImageIcon getEditIcon() {
        return new ImageIcon(Objects.requireNonNull(AbstractImageManager.class.getResource("/images/abstract/Green Edit Button.png")));
    }

    public static Image getMainMenuBackground() {
        try {
            return ImageIO.read(Objects.requireNonNull(AbstractImageManager.class.getResource("/images/abstract/Abstract MainMenu Background.png")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load main menu background image", e);
        }
    }

    public static ImageIcon getLogoIcon() {
        return new ImageIcon(Objects.requireNonNull(AbstractImageManager.class.getResource("/images/abstract/Abstract Logo Icon small.png")));
    }

    public static ImageIcon getMainTitleIcon(){
        return new ImageIcon(Objects.requireNonNull(AbstractImageManager.class.getResource("/images/abstract/Abstract Logo 1.png")));
    }

    public static Image getCreditsBackground() {
        try {
            return ImageIO.read(Objects.requireNonNull(AbstractImageManager.class.getResource("/images/abstract/Abstract Credits Background.png")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load credits background image", e);
        }
    }
}
