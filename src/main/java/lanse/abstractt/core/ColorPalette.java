package lanse.abstractt.core;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ColorPalette {

    public enum ColorCategory {
        BARS(new Color(30, 30, 30)),
        PRIMARY_BACKGROUND(new Color(40, 40, 40)),
        BUTTONS(new Color(60, 60, 60)),
        OUTLINE(new Color(130, 130, 130)),
        BUBBLES_AND_PROGRESS(new Color(130, 180, 255)),
        SUCCESS(new Color(60, 180, 100));

        private Color color;
        private final Color original;

        ColorCategory(Color color) {
            this.color = color;
            this.original = color;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color newColor) {
            this.color = newColor;
        }

        public void reset() {
            this.color = original;
        }

        public Color getOriginalColor() {
            return original;
        }
    }

    public static void randomizeColorPalette() {
        Random random = new Random();

        // reset to original first, so it doesn't stray too far
        for (ColorCategory category : ColorCategory.values()) {
            category.reset();
        }

        for (ColorCategory category : ColorCategory.values()) {
            Color base = category.getOriginalColor();
            float[] hsb = Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), null);
            float newHue = random.nextFloat();
            Color randomized = Color.getHSBColor(newHue, hsb[1], hsb[2]);
            category.setColor(randomized);
        }
    }

}
