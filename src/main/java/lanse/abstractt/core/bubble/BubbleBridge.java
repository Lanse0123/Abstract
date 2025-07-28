package lanse.abstractt.core.bubble;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BubbleBridge extends JPanel {
    private final Bubble a, b;
    private int width;
    private static final int BASE_WIDTH = 4;
    private static final Map<Set<Bubble>, BubbleBridge> allBridges = new HashMap<>();

    public BubbleBridge(Bubble a, Bubble b) {
        this.a = a;
        this.b = b;
        this.width = BASE_WIDTH;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        Point centerA = getBubbleCenter(a);
        Point centerB = getBubbleCenter(b);

        g2.setStroke(new BasicStroke(width));
        g2.setColor(new Color(250, 250, 250, 0));
        g2.drawLine(centerA.x, centerA.y, centerB.x, centerB.y);

        g2.dispose();
    }

    private Point getBubbleCenter(Bubble bubble) {
        Rectangle bounds = bubble.getBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    public void applyPullForce(Point2D.Double posA, Point2D.Double posB, Point2D.Double dispA, Point2D.Double dispB) {
        double dx = posA.x - posB.x;
        double dy = posA.y - posB.y;
        double dist = Math.max(0.01, Math.hypot(dx, dy));

        //TODO - balance these forces to work nicely with the gource map or something
        final double MIN_DISTANCE = 1000;
        final double PULL_STRENGTH = 0.005;
        final double PUSH_STRENGTH = 0.3;
        double force;

        if (dist < MIN_DISTANCE) {
            // Too close — push apart strongly
            force = (MIN_DISTANCE - dist) * PUSH_STRENGTH * width;
        } else {
            // Far apart — pull together weakly
            force = (dist - MIN_DISTANCE) * PULL_STRENGTH * width;
        }

        double fx = (dx / dist) * force;
        double fy = (dy / dist) * force;

        dispA.x -= fx;
        dispA.y -= fy;
        dispB.x += fx;
        dispB.y += fy;
    }


    public static void getOrCreate(Bubble a, Bubble b) {
        Set<Bubble> pair = Set.of(a, b); // unordered
        BubbleBridge bridge = allBridges.get(pair);
        if (bridge != null) {
            bridge.increaseWidth(1);
            return;
        }
        bridge = new BubbleBridge(a, b);
        allBridges.put(pair, bridge);
    }

    public void increaseWidth(int amount) { width += amount; }
    public int getWidth() { return width; }

    public Bubble getA() { return a; }
    public Bubble getB() { return b; }

    public static Collection<BubbleBridge> getAllBridges() { return allBridges.values(); }

    public static void clearAll() { allBridges.clear(); }
}