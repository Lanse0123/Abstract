package lanse.abstractt.core.bubble;

import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubblesortlogic.BubbleSorter;
import lanse.abstractt.core.screens.WorkSpaceScreen;
import lanse.abstractt.core.screens.bars.ProgressBarPanel;
import lanse.abstractt.parser.UniversalParser;
import lanse.abstractt.storage.AbstractImageManager;
import lanse.abstractt.storage.Storage;
import lanse.abstractt.storage.languages.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Bubble extends JPanel {

    private static final int DEFAULT_WIDTH = 680;
    private static final int DEFAULT_HEIGHT = 360;
    private static final int ICON_BASE_SIZE = 80;

    private static final ThreadFactory DAEMON_THREAD_FACTORY = r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    };
    protected static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), DAEMON_THREAD_FACTORY);
    public static boolean isABubbleBeingEdited = false;


    protected JLabel cancelIconLabel;
    protected JLabel editIconLabel;
    protected JLabel iconLabel;
    protected Icon cancelIcon;
    protected Icon editIcon;
    protected Icon icon;
    protected String title;
    protected String description;
    protected final String filePath;
    protected Color color;
    protected JComponent descriptionLabel;
    protected int width = DEFAULT_WIDTH;
    protected int height = DEFAULT_HEIGHT;
    public double scale = 1;
    private double lastZoom = -40404;

    public Bubble(String title, String description, String filePath, boolean isClickable) {
        this.title = title;
        this.description = description;
        this.filePath = filePath;

        this.icon = LanguageManager.getIconFromPath(filePath);
        this.editIcon = AbstractImageManager.getEditIcon();
        this.cancelIcon = AbstractImageManager.getEmptyIcon();
        this.color = LanguageManager.getLanguageColorFromPath(filePath, false);

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setLayout(new BorderLayout());

        initUI();
        registerMouseListeners(isClickable);
    }

    private void registerMouseListeners(boolean isClickable) {
        editIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
                handleEditClick(true);
            }
        });

        cancelIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isABubbleBeingEdited) {
                    new HandleClick(true).mouseClicked(e);
                }
                else {
                    e.consume();
                    handleEditClick(false);
                }
            }
        });

        addMouseListener(new HandleClick(isClickable));
    }

    public static void handleDirectory(File file, Container parent) {
        File[] children = file.listFiles();
        if (children == null) return;

        for (File child : children) {
            if (child.getName().equals("AbstractionVisualizerStorage")) continue;

            Bubble newBubble = Storage.load(child.getPath(), true);
            parent.setLayout(null);
            parent.add(newBubble);
        }
    }

    protected void initUI() {
        removeAll();
        lastZoom = -40404;

        iconLabel = new JLabel();
        editIconLabel = new JLabel();
        cancelIconLabel = new JLabel();

        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        editIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cancelIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        updateIconSize();

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

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        descriptionLabel = new JLabel("<html><body style='width: 220px'>" + description + "</body></html>");
        descriptionLabel.setForeground(Color.DARK_GRAY);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(descriptionLabel);
        centerPanel.setName("centerPanel");

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel, BorderLayout.WEST);
        leftPanel.add(divider, BorderLayout.EAST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(editIconLabel, BorderLayout.EAST);
        rightPanel.add(cancelIconLabel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape oval = new java.awt.geom.Ellipse2D.Double(0, 0, getWidth(), getHeight());
        g2.setClip(oval);

        g2.setColor((color != null && color != Color.BLACK) ? color : ColorPalette.ColorCategory.BUBBLES_AND_PROGRESS.getColor());

        g2.fill(oval);

        g2.setColor(ColorPalette.ColorCategory.OUTLINE.getColor());
        g2.setStroke(new BasicStroke(8));
        g2.draw(oval);

        updateIconSize();

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public void setSize(int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = (int) (width * this.scale);
            this.height = (int) (height * this.scale);
            setPreferredSize(new Dimension(width, height));
        }
    }

    public void updateIconSize() {
        double zoom = WorldMap.getZoom();
        if (zoom == lastZoom) return;

        lastZoom = zoom;
        int size = (int) (ICON_BASE_SIZE * zoom);

        scaleIcon(icon, iconLabel, size);
        scaleIcon(editIcon, editIconLabel, size);
        scaleIcon(cancelIcon, cancelIconLabel, size);
    }

    private void scaleIcon(Icon icon, JLabel label, int size) {
        if (icon instanceof ImageIcon imgIcon) {
            Image scaled = imgIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        }
    }

    private void handleEditClick(boolean isEditButton) {

        //TODO - somehow, it sometimes takes the empty pixel and brings it here, labeling it as the edit button

        JPanel centerPanel = (JPanel) Arrays.stream(this.getComponents()).filter(comp ->
                comp instanceof JPanel && comp.getName() != null && comp.getName().contains("centerPanel")).findFirst().get();

        centerPanel.remove(descriptionLabel);

        if (isABubbleBeingEdited){
            //logic to stop the bubble from being edited
            isABubbleBeingEdited = false;
            this.editIcon = AbstractImageManager.getEditIcon();
            this.cancelIcon = AbstractImageManager.getEmptyIcon();

            if (isEditButton) {
                description = ((JTextArea) descriptionLabel).getText();

                System.out.println("Stopping editing " + title);
                if (this instanceof FunctionBubble) {
                    Storage.updateStructure(this.filePath, ((FunctionBubble) this).structure, this.title,
                            Optional.ofNullable(this.description), Optional.empty(), Optional.empty());
                } else {
                    Storage.save(this);
                }
            }
            else {
                System.out.println("Cancelling editing " + title);
            }

            descriptionLabel = new JLabel("<html><body style='width: 220px'>" + description + "</body></html>");
        }
        else {
            if (isEditButton) {
                //logic for starting to edit a description
                isABubbleBeingEdited = true;
                this.editIcon = AbstractImageManager.getCheckMarkIcon();
                this.cancelIcon = AbstractImageManager.getCancelIcon();
                System.out.println("About to edit: " + title);

                JTextArea textArea = new JTextArea(this.description);
                textArea.setEditable(true);
                textArea.setLineWrap(true);
                this.descriptionLabel = textArea;
            }
        }

        descriptionLabel.setForeground(Color.DARK_GRAY);
        descriptionLabel.setBackground(new Color(0, 0, 0, 0));
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(descriptionLabel);
        getParent().revalidate();
        getParent().repaint();
        lastZoom = -40404;
    }

    public String getFilePath() { return filePath; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    private class HandleClick extends MouseAdapter {
        private final boolean isClickable;

        public HandleClick(boolean isClickable) {
            this.isClickable = isClickable;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!isClickable || ProgressBarPanel.isLoading()) return;
            if (e.getSource() == editIconLabel) return;
            if (isABubbleBeingEdited) return;

            File file = new File(filePath);
            if (!file.exists()) {
                JLabel error = new JLabel("Invalid file path: " + title, SwingConstants.CENTER);
                error.setForeground(Color.RED);
                Container parent = getParent();
                if (parent != null) parent.add(error);
                return;
            }

            Storage.increaseDepth(filePath);

            Container parent = getParent();
            if (parent == null) return;

            Storage.saveAllBubbles(true, parent);

            if (file.isDirectory()) {
                handleDirectory(file, parent);
            } else {
                executor.submit(() -> UniversalParser.handleFile(filePath, parent));
            }

            WorldMap.setCameraCoordinates(0, 0);
            if (parent instanceof WorkSpaceScreen workspace) workspace.refreshSidebar();
            parent.revalidate();
            parent.repaint();
            BubbleSorter.isSorted = false;
        }
    }
}
