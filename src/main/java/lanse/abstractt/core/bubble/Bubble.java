package lanse.abstractt.core.bubble;

import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.core.DisplayModeSelector;
import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.core.screens.WorkSpaceScreen;
import lanse.abstractt.storage.Storage;
import lanse.abstractt.storage.languages.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Bubble extends JPanel {

    protected String title;
    protected String description;
    protected Icon icon;
    protected final String filePath;
    protected int width = 680;
    protected int height = 360;
    private JLabel iconLabel;
    private double lastZoom = -40404;

    //TODO - make use of isClickable. Some bubbles like Description bubbles should not clear other bubbles when clicked
    private boolean isClickable = true;

    public Bubble(String title, String description, String filePath) {
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.icon = LanguageManager.getIconFromPath(filePath);

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setLayout(new BorderLayout());
        initUI();

        //Click handler
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (ProgressBarPanel.isLoading()) return;

                //TODO - this is where isClickable might come in handy. I might need a better name for it.
                Storage.increaseDepth(filePath);
                DisplayModeSelector.clearBubbles();

                Container parent = getParent();
                if (parent == null) return;

                for (Component comp : parent.getComponents()) {
                    if (comp instanceof Bubble) {
                        Storage.save((Bubble) comp);
                        parent.remove(comp);
                    }
                }

                File file = new File(filePath);
                if (!file.exists()) {
                    JLabel error = new JLabel("Invalid file path: " + title, SwingConstants.CENTER);
                    error.setForeground(Color.RED);
                    parent.add(error);
                }

                if (file.isDirectory()) {
                    handleDirectory(file, parent);
                } else {
                    handleFile(filePath, parent);
                }

                WorldMap.setCameraCoordinates(0, 0);

                if (parent instanceof WorkSpaceScreen workspace) workspace.refreshSidebar();

                parent.revalidate();
                parent.repaint();
            }
        });
    }

    public static void handleFile(String filePath, Container parent){
        //TODO - FIRST: check if the file's extension is in the JSON list.
        // If yes, then check it's parse value. If it should parse, continue on. Then, just open it like a txt file,
        // and make a code view bubble (which is actually going to be a rectangle). There will be exceptions like PNG

        //TODO - NEXT: (if continuing), go through each line. Add the line to a prompt to mobiLlama. If the line is
        // less than 200 or so characters, add the next line. Keep adding lines as long as it doesn't become too big of a prompt.
        // add line numbers for reference.

        //TODO - then, ask mobiLlama if it is containing anything important, like fields, classes, functions, imports, or something else.
        // It should either respond with NO, or line number + what it is. Make sure it has context like the language type, and file name.

        //TODO - FINALLY, once it has done this for all the lines in that file, use it to create the bubbles like handleDirectory does.
        // each of these bubbles should have the class / file name. If there is more than 1 class, create class bubbles, and
        // those class bubbles will contain the function bubbles. If there is only 1 class, make the bubbles split by functions.
        // There should also be an imports and fields bubble. Each function bubble should be able to see fields if the option is enabled by the user.

        //TODO - LASTLY, make sure this is added to storage, so it never needs to do this again unless the code is edited, or
        // if the user refreshes it. It can also do this smartly, by saving the code, and checking for parts that changed.
        // then only call the AI for those parts, because going through each line each update might take a while.
    }

    public static void handleDirectory(File file, Container parent){
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {

                String childTitle = child.getName();

                if (childTitle.equals("AbstractionVisualizerStorage")) continue;

                Bubble newBubble = Storage.load(child.getPath());

                parent.setLayout(null);
                parent.add(newBubble);
            }
        }
    }

    protected void initUI() {
        // scale icon
        iconLabel = new JLabel();
        updateIconSize(); // set icon at init
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // VERTICAL DIVIDER
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(ColorPalette.ColorCategory.OUTLINE.getColor());
                int y = (getHeight() - 160) / 2;
                g.fillRect(getWidth() / 2, y, 1, 160);
            }
        };
        divider.setPreferredSize(new Dimension(10, height));
        divider.setOpaque(false);

        // CENTER: title + description
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("<html><body style='width: 220px'>" + description + "</body></html>");
        descriptionLabel.setForeground(Color.DARK_GRAY);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(descriptionLabel);

        // RIGHT placeholder
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(30, 30));
        rightPanel.setOpaque(false);

        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.add(iconLabel, BorderLayout.WEST);
        leftWrapper.add(divider, BorderLayout.EAST);

        add(leftWrapper, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define oval shape and clip to it
        Shape oval = new java.awt.geom.Ellipse2D.Double(0, 0, getWidth(), getHeight());
        g2.setClip(oval);

        //TODO - I will eventually make this use the color tag in the language's JSON file, and folders or other randoms will be light blue
        g2.setColor(ColorPalette.ColorCategory.BUBBLES_AND_PROGRESS.getColor());
        g2.fill(oval);

        // Draw outline
        g2.setColor(ColorPalette.ColorCategory.OUTLINE.getColor());
        g2.setStroke(new BasicStroke(8));
        g2.draw(oval);

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
        double zoom = WorldMap.getZoomStatic();
        if (zoom == lastZoom) return;

        lastZoom = zoom;
        int size = (int) (80 * zoom);

        if (icon instanceof ImageIcon imgIcon) {
            Image scaledImage = imgIcon.getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}