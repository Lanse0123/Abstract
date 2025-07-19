package lanse.abstractt.core.bubble;

import lanse.abstractt.core.WorldMap;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PictureBubble extends JPanel {

    private double lastZoom = -40404;
    protected Icon icon;
    private JLabel iconLabel;
    protected int width;
    protected int height;

    //TODO - these need to be a few times bigger than what they start at on default...
    public PictureBubble(String filePath){
        this.icon = new ImageIcon(filePath);
        this.width = icon.getIconWidth();
        this.height = icon.getIconHeight();

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setLayout(new BorderLayout());
        initUI();
    }

    public static void createPictureBubble(String filePath, Container parent){

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            return;
        }

        PictureBubble pictureBubble = new PictureBubble(filePath);
        parent.add(pictureBubble);
        System.out.println("Created Picture Bubble and Added it to Parent!");

        parent.revalidate();
        parent.repaint();
    }

    private void initUI() {

        iconLabel = new JLabel();
        updateIconSize(); // set icon at init
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.add(iconLabel, BorderLayout.WEST);

        add(leftWrapper, BorderLayout.WEST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //this should now only do anything if the scale changed
        updateIconSize();

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public void setSize(int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            setPreferredSize(new Dimension(width, height));
        }
    }

    public void updateIconSize() {
        double zoom = WorldMap.getZoom();
        if (zoom == lastZoom) return;

        lastZoom = zoom;
        int size = (int) (80 * zoom);

        if (icon instanceof ImageIcon imgIcon) {
            Image scaledImage = imgIcon.getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        }
    }
}
