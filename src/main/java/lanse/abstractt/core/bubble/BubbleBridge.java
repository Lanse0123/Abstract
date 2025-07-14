package lanse.abstractt.core.bubble;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BubbleBridge extends JPanel {

    //TODO - this class will be used to connect bubbles in big maps. For example, making GourceMap would use these
    // to connect directories or something.
    private final List<Line> lines;
    public record Line(Point p1, Point p2) {}

    public BubbleBridge(List<Line> lines) {
        this.lines = lines;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(100, 100, 100, 128));
        for (Line line : lines) {
            g2.drawLine(line.p1.x, line.p1.y, line.p2.x, line.p2.y);
        }
        g2.dispose();
    }
}

