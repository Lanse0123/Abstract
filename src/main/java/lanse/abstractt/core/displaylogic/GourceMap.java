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
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int ITERATIONS = 500;
    private static final double AREA = WIDTH * HEIGHT;
    private static final double K = Math.sqrt(AREA / 100); // optimal distance

    private static class Node {
        Point2D.Double pos, disp = new Point2D.Double();

        Node(double x, double y) {
            pos = new Point2D.Double(x, y);
        }
    }

    public static Map<Bubble, Point> getLayout(Bubble[] bubbles, Component[] staticBubbles, WorldMap worldMap) {
        Map<Bubble, Point> layout = new HashMap<>();
        List<Point> edges = new ArrayList<>();

        int nodeCount = bubbles.length;
        Node[] nodes = new Node[nodeCount];
        Random rnd = new Random();

        // Map: absolutePath → index in bubbles
        Map<String, Integer> pathToIndex = new HashMap<>();
        for (int i = 0; i < nodeCount; i++) {
            pathToIndex.put(new File(bubbles[i].getFilePath()).getAbsolutePath(), i);
            nodes[i] = new Node(rnd.nextDouble() * WIDTH, rnd.nextDouble() * HEIGHT);
        }

        // Create parent-child edges
        for (int i = 0; i < nodeCount; i++) {
            File file = new File(bubbles[i].getFilePath());
            File parent = file.getParentFile();
            if (parent != null) {
                String parentPath = parent.getAbsolutePath();
                Integer parentIndex = pathToIndex.get(parentPath);
                if (parentIndex != null) {
                    edges.add(new Point(parentIndex, i));
                }
            }
        }

        // Run force-directed layout
        for (int iter = 0; iter < ITERATIONS; iter++) {
            for (Node v : nodes) v.disp.setLocation(0, 0);

            for (int i = 0; i < nodeCount; i++) {
                for (int j = i + 1; j < nodeCount; j++) {
                    Node v = nodes[i], u = nodes[j];
                    double dx = v.pos.x - u.pos.x;
                    double dy = v.pos.y - u.pos.y;
                    double dist = Math.max(0.01, Math.hypot(dx, dy));
                    double force = (K * K) / dist;
                    double fx = (dx / dist) * force;
                    double fy = (dy / dist) * force;
                    v.disp.x += fx;
                    v.disp.y += fy;
                    u.disp.x -= fx;
                    u.disp.y -= fy;
                }
            }

            for (Point e : edges) {
                Node v = nodes[e.x], u = nodes[e.y];
                double dx = v.pos.x - u.pos.x;
                double dy = v.pos.y - u.pos.y;
                double dist = Math.max(0.01, Math.hypot(dx, dy));
                double force = (dist * dist) / K;
                double fx = (dx / dist) * force;
                double fy = (dy / dist) * force;
                v.disp.x -= fx;
                v.disp.y -= fy;
                u.disp.x += fx;
                u.disp.y += fy;
            }

            for (Node v : nodes) {
                double dispLength = Math.max(0.01, Math.hypot(v.disp.x, v.disp.y));
                double min = Math.min(dispLength, 10);
                v.pos.x += (v.disp.x / dispLength) * min;
                v.pos.y += (v.disp.y / dispLength) * min;
                v.pos.x = Math.min(WIDTH, Math.max(0, v.pos.x));
                v.pos.y = Math.min(HEIGHT, Math.max(0, v.pos.y));
            }
        }

        for (int i = 0; i < nodeCount; i++) {
            Bubble b = bubbles[i];
            Point2D.Double pos = nodes[i].pos;
            layout.put(b, new Point((int) pos.x, (int) pos.y));
        }

        // Static components
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