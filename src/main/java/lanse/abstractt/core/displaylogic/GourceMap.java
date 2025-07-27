package lanse.abstractt.core.displaylogic;

import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.screens.WorkSpaceScreen;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.List;

public class GourceMap {
    private static final int ITERATIONS = 500;
    private static final double WIDTH = 1920;
    private static final double HEIGHT = 1080;
    private static final double AREA = WIDTH * HEIGHT;
    private static final double K = Math.sqrt(AREA / 100); // optimal distance
    private static final Point2D.Double CENTER = new Point2D.Double(WIDTH / 2, HEIGHT / 2);
    private static final double GRAVITY = 0.05;

    private static class Node {
        Point2D.Double pos;
        Point2D.Double disp = new Point2D.Double();

        Node(double x, double y) {
            this.pos = new Point2D.Double(x, y);
        }
    }

    public static Map<Bubble, Point> getLayout(Bubble[] bubbles, Component[] staticBubbles, WorldMap worldMap) {
        Map<Bubble, Point> layout = new HashMap<>();
        List<Point> edges = new ArrayList<>();

        int nodeCount = bubbles.length;
        Node[] nodes = new Node[nodeCount];
        Random rnd = new Random();

        // Map file path to index
        Map<String, Integer> pathToIndex = new HashMap<>();
        for (int i = 0; i < nodeCount; i++) {
            String absPath = new File(bubbles[i].getFilePath()).getAbsolutePath();
            pathToIndex.put(absPath, i);
            nodes[i] = new Node(rnd.nextDouble() * WIDTH, rnd.nextDouble() * HEIGHT);
        }

        // Create parent-child edges
        for (int i = 0; i < nodeCount; i++) {
            File file = new File(bubbles[i].getFilePath());
            File parent = file.getParentFile();
            if (parent != null) {
                Integer parentIndex = pathToIndex.get(parent.getAbsolutePath());
                if (parentIndex != null) {
                    edges.add(new Point(parentIndex, i));
                }
            }
        }

        // Force-directed layout
        for (int iter = 0; iter < ITERATIONS; iter++) {
            for (Node node : nodes) node.disp.setLocation(0, 0);

            // Repulsion
            for (int i = 0; i < nodeCount; i++) {
                for (int j = i + 1; j < nodeCount; j++) {
                    Node v = nodes[i], u = nodes[j];
                    double dx = v.pos.x - u.pos.x;
                    double dy = v.pos.y - u.pos.y;
                    double dist = Math.max(0.01, Math.hypot(dx, dy));
                    double force = (K * K) / dist;
                    double fx = (dx / dist) * force;
                    double fy = (dy / dist) * force;
                    v.disp.x += fx; v.disp.y += fy;
                    u.disp.x -= fx; u.disp.y -= fy;
                }
            }

            // Attraction
            for (Point edge : edges) {
                Node v = nodes[edge.x], u = nodes[edge.y];
                double dx = v.pos.x - u.pos.x;
                double dy = v.pos.y - u.pos.y;
                double dist = Math.max(0.01, Math.hypot(dx, dy));
                double force = (dist * dist) / K;
                double fx = (dx / dist) * force;
                double fy = (dy / dist) * force;
                v.disp.x -= fx; v.disp.y -= fy;
                u.disp.x += fx; u.disp.y += fy;
            }

            // Gravity pull to center
            for (Node v : nodes) {
                double dx = CENTER.x - v.pos.x;
                double dy = CENTER.y - v.pos.y;
                v.disp.x += dx * GRAVITY;
                v.disp.y += dy * GRAVITY;
            }

            // Apply displacement
            for (Node v : nodes) {
                double dispLength = Math.max(0.01, Math.hypot(v.disp.x, v.disp.y));
                double scale = Math.min(dispLength, 10);
                v.pos.x += (v.disp.x / dispLength) * scale;
                v.pos.y += (v.disp.y / dispLength) * scale;
                // No clamping here — nodes can sprawl
            }
        }

        // Final layout map
        for (int i = 0; i < nodeCount; i++) {
            Point2D.Double pos = nodes[i].pos;
            layout.put(bubbles[i], new Point((int) pos.x, (int) pos.y));
        }

        // Position static components
        double zoom = WorldMap.getZoom();
        for (Component bubble : staticBubbles) {
            Point screenPos = worldMap.transform(1500, 480);
            int width = (int) (bubble.getPreferredSize().width * zoom);
            int height = (int) (bubble.getPreferredSize().height * zoom);
            bubble.setBounds(screenPos.x + WorkSpaceScreen.SIDEBAR_WIDTH, screenPos.y, width, height);
        }

        return layout;
    }
}