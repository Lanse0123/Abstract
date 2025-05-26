package lanse.abstractt.core.bubble;

import lanse.abstractt.core.DisplayModeSelector;
import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.screens.ProgressBarPanel;
import lanse.abstractt.core.screens.WorkSpaceScreen;
import lanse.abstractt.storage.Storage;

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
    private double posX = 0;
    private double posY = 0;
    protected int width = 680;
    protected int height = 360;

    public Bubble(String title, String description, Icon icon, String filePath) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.filePath = filePath;

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setLayout(new BorderLayout());
        initUI();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (ProgressBarPanel.isLoading()) return;

                Storage.increaseDepth(filePath);
                int newDepth = Storage.getDepth();
                Storage.setCurrentDepth(newDepth);

                Container parent = getParent();
                if (parent != null) {
                    for (Component comp : parent.getComponents()) {
                        if (comp instanceof Bubble) {
                            parent.remove(comp);
                        }
                    }

                    //TODO - this code is more nested and id kinda like, clean this at some point plz
                    File file = new File(filePath);
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            File[] children = file.listFiles();
                            if (children != null) {
                                for (File child : children) {

                                    String childTitle = child.getName();
                                    String childPath = child.getPath();
                                    String childDescription = (child.isDirectory() ? "Folder" : "File") + ": " + child.getName();
                                    Icon childIcon = UIManager.getIcon(child.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");

                                    if (childTitle.equals("AbstractionVisualizerStorage")) continue;

                                    Point pos = DisplayModeSelector.getNewBubblePosition();
                                    Bubble newBubble = new Bubble(childTitle, childDescription, childIcon, childPath);
                                    newBubble.setPos(pos.getX(), pos.getY());

                                    //TODO: can we remove this?
                                    Storage.addBubble(newDepth, newBubble);
                                    parent.setLayout(null);
                                    newBubble.setBounds(pos.x, pos.y, newBubble.width, newBubble.height);
                                    parent.add(newBubble);
                                }
                            }
                        } else {
                            String fileTitle = file.getName();
                            String filePath = file.getPath();
                            String fileDescription = "File: " + file.getName();
                            Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

                            Point pos = DisplayModeSelector.getNewBubblePosition();
                            Bubble fileBubble = new Bubble(fileTitle, fileDescription, fileIcon, filePath);
                            fileBubble.setPos(pos.getX(), pos.getY());

                            Storage.addBubble(newDepth, fileBubble);
                            parent.setLayout(null);
                            fileBubble.setBounds(pos.x, pos.y, fileBubble.width, fileBubble.height);
                            parent.add(fileBubble);
                        }
                    } else {
                        JLabel error = new JLabel("Invalid file path: " + title, SwingConstants.CENTER);
                        error.setForeground(Color.RED);
                        parent.add(error);
                    }

                    WorldMap.setCameraCoordinates(0, 0);

                    if (parent instanceof WorkSpaceScreen workspace) workspace.refreshSidebar();

                    parent.revalidate();
                    parent.repaint();
                }
            }
        });
    }

    protected void initUI() {
        // LEFT: icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // VERTICAL DIVIDER
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
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

        // Fill with light blue
        //TODO - I might eventually make the color based on the average pixel colors of an Icon, with unknowns being light gray or light blue (dont do this yet)
        g2.setColor(new Color(130, 180, 255));
        g2.fill(oval);

        // Draw outline
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(8));
        g2.draw(oval);

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

    public double getPosX() { return posX; }
    public double getPosY() { return posY; }

    public void setPos(double x, double y) {
        this.posX = x;
        this.posY = y;
    }
}