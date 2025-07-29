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

    public BubbleBridge(Bubble a, Bubble b, Container parent) {
        this.a = a;
        this.b = b;
        this.width = BASE_WIDTH;
        setOpaque(false); // set transparent for custom drawing

        //TODO - setPreferredSize(new Dimension(width, height)); never happens here

        setLayout(new BorderLayout());
        parent.add(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateBridgeBounds(); // still needed to adjust size

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Point centerA = getBubbleCenter(a);
        Point centerB = getBubbleCenter(b);

        Point offset = getLocation();
        int ax = centerA.x - offset.x;
        int ay = centerA.y - offset.y;
        int bx = centerB.x - offset.x;
        int by = centerB.y - offset.y;

        // Draw rectangle "bridge" between points A and B
        double dx = bx - ax;
        double dy = by - ay;
        double len = Math.hypot(dx, dy);
        double angle = Math.atan2(dy, dx);

        Graphics2D bridgeG = (Graphics2D) g2.create();
        bridgeG.translate(ax, ay);
        bridgeG.rotate(angle);

        bridgeG.setColor(Color.RED);
        bridgeG.drawRect(0, -getWidth() / 2, (int) len, getHeight());

        bridgeG.dispose();
//
//        // Debug yellow border
//        g2.setColor(Color.YELLOW);
//        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g2.dispose();
    }

    private Point getBubbleCenter(Bubble bubble) {
        Rectangle bounds = bubble.getBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    public void updateBridgeBounds() {
        Point aCenter = getBubbleCenter(a);
        Point bCenter = getBubbleCenter(b);

        int minX = Math.min(aCenter.x, bCenter.x);
        int minY = Math.min(aCenter.y, bCenter.y);
        int maxX = Math.max(aCenter.x, bCenter.x);
        int maxY = Math.max(aCenter.y, bCenter.y);

        int bridgeWidth = Math.max(1, maxX - minX);
        int bridgeHeight = Math.max(1, maxY - minY);

        int pad = width + 4;

        // Set bounds in parent coordinates
        setBounds(minX - pad, minY - pad, bridgeWidth + pad * 2, bridgeHeight + pad * 2);

        repaint();
        revalidate();
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
            // Too close, push apart strongly
            force = (MIN_DISTANCE - dist) * PUSH_STRENGTH * width;
        } else {
            // Far apart, pull together weakly
            force = (dist - MIN_DISTANCE) * PULL_STRENGTH * width;
        }

        double fx = (dx / dist) * force;
        double fy = (dy / dist) * force;

        dispA.x -= fx;
        dispA.y -= fy;
        dispB.x += fx;
        dispB.y += fy;
    }

    public static void getOrCreate(Bubble a, Bubble b, Container parent) {
        Set<Bubble> pair = Set.of(a, b); // unordered
        BubbleBridge bridge = allBridges.get(pair);
        if (bridge != null) {
            bridge.increaseWidth(1);
            return;
        }
        bridge = new BubbleBridge(a, b, parent);
        allBridges.put(pair, bridge);
    }

    public void increaseWidth(int amount) { width += amount; }
    public int getWidth() { return width; }

    public Bubble getA() { return a; }
    public Bubble getB() { return b; }

    public static Collection<BubbleBridge> getAllBridges() { return allBridges.values(); }

    public static void clearAll() { allBridges.clear(); }
}