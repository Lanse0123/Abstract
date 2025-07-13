package lanse.abstractt.core.bubble;

import lanse.abstractt.core.ColorPalette;
import lanse.abstractt.core.WorldMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CodeBubble extends JPanel {

    protected String fileContents;
    protected Color color;
    protected int width;
    protected int height;
    private double lastZoom = -40404;
    private JLabel codeLabel;

    public CodeBubble(String fileContents, int width, int height) {
        this.fileContents = fileContents;
        this.width = width;
        this.height = height;
        this.color = new Color(15, 15, 15);

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setLayout(new BorderLayout());
        initUI();

        //Click handler
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//
//                //TODO - logic for clicking and editing the text can go here.
//
//                Container parent = getParent();
//
//                parent.revalidate();
//                parent.repaint();
//            }
//        });
    }

    public static void createCodeBubble(String filePath, Container parent){

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            return;
        }

        int width;
        int height;
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int lineNumber = 1;
            int widestLine = (int) -0XABCDEL;
            String line;

            while ((line = reader.readLine()) != null) {

                if (widestLine <= line.length()) widestLine = line.length();

                //TODO - if the language is assembly, use hexadecimal to represent the line number, because funny
                String lineWithNumber = "  " + lineNumber + "." + " ".repeat(8 - Integer.valueOf(lineNumber).toString().length()) + line + "<br>";

                fileContents.append(lineWithNumber);
                lineNumber++;
            }

            //TODO - might need more testing, but these magical numbers somehow work, as well as base font being 16??
            width = widestLine * 12 + 20;
            height = lineNumber * 22 + 40;

        } catch (IOException e) {
            System.err.println("Failed to read file: " + filePath);
            e.printStackTrace();
            return;
        }

        CodeBubble codeBubble = new CodeBubble(fileContents.toString(), width, height);
        parent.add(codeBubble);
        System.out.println(fileContents);
    }

    protected void initUI() {

        //TODO - remake this for text and new format

        // CENTER: title + description
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // RENDERS THE CODE
        codeLabel = new JLabel();
        updateCodeLabelFont();

        codeLabel.setForeground(Color.WHITE);
        codeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(codeLabel);

        // RIGHT placeholder
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(30, 30));
        rightPanel.setOpaque(false);

        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setOpaque(false);

        add(leftWrapper, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        updateCodeLabelFont();

        // Define rectangle shape and clip to it
        Shape rectangle = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g2.setClip(rectangle);

        if (color != null && color != Color.BLACK){
            g2.setColor(color);
        } else {
            g2.setColor(ColorPalette.ColorCategory.BUBBLES_AND_PROGRESS.getColor());
        }

        g2.fill(rectangle);

        // Draw outline
        g2.setColor(ColorPalette.ColorCategory.OUTLINE.getColor());
        g2.setStroke(new BasicStroke(8));
        g2.draw(rectangle);

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

    private void updateCodeLabelFont() {
        double zoom = WorldMap.getZoomStatic();
        if (zoom == lastZoom) return;
        lastZoom = zoom;

        int baseFontSize = 16;
        int scaledFontSize = (int) (baseFontSize * zoom);

        Font newFont = new Font("Monospaced", Font.PLAIN, scaledFontSize);
        codeLabel.setFont(newFont);

        // You may need to adjust width hints here too
        String html = "<html><body style='width: " + (int)(width * zoom * 0.9) + "px'><pre>" + fileContents + "</pre></body></html>";
        codeLabel.setText(html);
    }

}
