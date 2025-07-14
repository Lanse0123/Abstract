package lanse.abstractt.core.displaylogic;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class GourceMap {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int ITERATIONS = 500;
    private static final double AREA = WIDTH * HEIGHT;
    private static final double K = Math.sqrt(AREA / 100); // optimal distance

    private static class Node {
        Point2D.Double pos, disp = new Point2D.Double();
        Node(double x, double y) { pos = new Point2D.Double(x, y); }
    }

    //TODO - this is all BS example code. I just have it here so I can get an idea of what I might want to make use of with this.

    public static Point[] getGourceMap(int nodeCount, java.util.List<Point> edges) {
        // Initialize nodes at random
        Node[] nodes = new Node[nodeCount];
        Random rnd = new Random();
        for (int i = 0; i < nodeCount; i++) {
            nodes[i] = new Node(rnd.nextDouble() * WIDTH, rnd.nextDouble() * HEIGHT);
        }

        // Iteratively apply repulsion and attraction
        for (int iter = 0; iter < ITERATIONS; iter++) {
            // reset displacements
            for (Node v : nodes) {
                v.disp.setLocation(0, 0);
            }

            // Repulsive force between all pairs
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

            // Attractive force along edges
            for (Point e : edges) {
                Node v = nodes[e.x], u = nodes[e.y];
                double dx = v.pos.x - u.pos.x;
                double dy = v.pos.y - u.pos.y;
                double dist = Math.max(0.01, Math.hypot(dx, dy));
                double force = (dist * dist) / K;
                double fx = (dx / dist) * force;
                double fy = (dy / dist) * force;
                v.disp.x -= fx; v.disp.y -= fy;
                u.disp.x += fx; u.disp.y += fy;
            }

            // Limit max displacement and apply to positions
            for (Node v : nodes) {
                double dispLength = Math.max(0.01, Math.hypot(v.disp.x, v.disp.y));
                double min = Math.min(dispLength, 10);  // cap per-iteration move
                v.pos.x += (v.disp.x / dispLength) * min;
                v.pos.y += (v.disp.y / dispLength) * min;
                // keep within bounds
                v.pos.x = Math.min(WIDTH, Math.max(0, v.pos.x));
                v.pos.y = Math.min(HEIGHT, Math.max(0, v.pos.y));
            }
        }

        // convert to integer Points
        Point[] points = new Point[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            points[i] = new Point((int)nodes[i].pos.x, (int)nodes[i].pos.y);
        }
        return points;
    }
}
