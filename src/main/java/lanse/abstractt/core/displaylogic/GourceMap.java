package lanse.abstractt.core.displaylogic;

import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.BubbleBridge;
import lanse.abstractt.core.screens.WorkSpaceScreen;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.List;

public class GourceMap {
    //TODO - eventually, I want to connect lines to each bubble using the BubbleBridge class.
    // each bubble should make a bubbleBridge to it's parent. Once this was done for all,
    // remove all of the directory bubbles except TopBubble.
    private static final int ITERATIONS = 1000; //TODO - this should be at 500 by default. Make something to change this.
    private static final double WIDTH = 1920, HEIGHT = 1080;
    private static double K; // nominal edge length (?)
    private static final Point2D.Double CENTER = new Point2D.Double(WIDTH/2, HEIGHT/2);

    private static final double GRAVITY = 0.05;
    private static final double MIN_DIST = 1000; // Prevent overlap radius
    private static final double MAX_DISP = 750; // Allow wider motion per step
    private static final double REPULSION_MULT = 12;
    private static final double CLOSE_REPULSION_BOOST = 42.0;
    private static final double ATTRACTION_MULT = 0.5;


    // Internal node representation
    private static class Node {
        Point2D.Double pos, disp = new Point2D.Double();
        Node(double x, double y) { pos = new Point2D.Double(x, y); }
    }

    private static boolean initialized = false;
    private static Node[] nodes;
    private static List<Point> edges;
    private static List<Bubble> bubblesRef;

    private static void init(Bubble[] bubbles) {
        K = computeK(bubbles.length);
        bubblesRef = Arrays.asList(bubbles);
        int n = bubbles.length;
        nodes = new Node[n];
        edges = new ArrayList<>(n);

        // random start positions
        Random rnd = new Random();
        Map<String,Integer> pathToIndex = new HashMap<>(n);
        for (int i = 0; i < n; i++) {
            pathToIndex.put(new File(bubbles[i].getFilePath()).getAbsolutePath(), i);
            nodes[i] = new Node(rnd.nextDouble()*WIDTH, rnd.nextDouble()*HEIGHT);
        }

        BubbleBridge.clearAll();

        // parent→child edges
        for (int i = 0; i < n; i++) {
            File f = new File(bubbles[i].getFilePath());
            File p = f.getParentFile();
            if (p != null) {
                Integer pi = pathToIndex.get(p.getAbsolutePath());
                if (pi != null){
                    edges.add(new Point(pi, i));
                    BubbleBridge.getOrCreate(bubbles[pi], bubbles[i]);
                }
            }
        }
        initialized = true;
    }

    //its insane how computers can do this so fast
    public static void step() {
        if (!initialized) throw new IllegalStateException("GourceMap not initialized");
        int n = nodes.length;

        // reset displacements
        for (Node v : nodes) v.disp.setLocation(0, 0);

        // repulsive forces
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Node vi = nodes[i], vj = nodes[j];
                double dx = vi.pos.x - vj.pos.x, dy = vi.pos.y - vj.pos.y;
                double dist = Math.max(0.01, Math.hypot(dx, dy));

                // Boost repulsion if too close
                double boost = dist < MIN_DIST ? CLOSE_REPULSION_BOOST : REPULSION_MULT;
                double force = ((K * K) / dist) * boost;

                double fx = (dx / dist) * force;
                double fy = (dy / dist) * force;

                vi.disp.x += fx; vi.disp.y += fy;
                vj.disp.x -= fx; vj.disp.y -= fy;
            }
        }

        // attractive forces along edges
        for (Point e : edges) {
            Node a = nodes[e.x], b = nodes[e.y];
            double dx = a.pos.x - b.pos.x, dy = a.pos.y - b.pos.y;
            double dist = Math.max(0.01, Math.hypot(dx, dy));
            double force = (dist * dist / K) * ATTRACTION_MULT;

            double fx = (dx / dist) * force;
            double fy = (dy / dist) * force;

            a.disp.x -= fx; a.disp.y -= fy;
            b.disp.x += fx; b.disp.y += fy;
        }

        //TODO - make sure bubble bridges and attraction / repulsion is balanced.

        // bubble bridge attraction
        for (BubbleBridge bridge : BubbleBridge.getAllBridges()) {
            int ai = bubblesRef.indexOf(bridge.getA());
            int bi = bubblesRef.indexOf(bridge.getB());
            if (ai == -1 || bi == -1) continue;

            Node na = nodes[ai];
            Node nb = nodes[bi];

            bridge.applyPullForce(na.pos, nb.pos, na.disp, nb.disp);
        }

        // gravity toward center
        for (Node v : nodes) {
            double dx = CENTER.x - v.pos.x, dy = CENTER.y - v.pos.y;
            v.disp.x += dx * GRAVITY;
            v.disp.y += dy * GRAVITY;
        }

        // apply displacements
        for (Node v : nodes) {
            double dlen = Math.max(0.01, Math.hypot(v.disp.x, v.disp.y));
            double scale = Math.min(dlen, MAX_DISP);
            v.pos.x += (v.disp.x / dlen) * scale;
            v.pos.y += (v.disp.y / dlen) * scale;
        }
    }

    public static void clear() {
        initialized = false;
        nodes = null;
        edges = null;
    }

    public static Map<Bubble,Point> getLayout(Bubble[] bubbles, Component[] staticBubbles, WorldMap worldMap) {
        if (!initialized || !bubblesRef.equals(Arrays.asList(bubbles))){
            init(bubbles);
            for (int i = 0; i < ITERATIONS; i++) step();
        }

        // build the map from Node positions
        Map<Bubble,Point> layout = new HashMap<>(bubbles.length);
        for (int i = 0; i < bubbles.length; i++) {
            Point2D.Double p = nodes[i].pos;
            layout.put(bubbles[i], new Point((int)p.x, (int)p.y));
        }

        // position any static components
        double zoom = WorldMap.getZoom();
        for (Component c: staticBubbles) {
            Point screenPos = worldMap.transform(1500,480);
            int w = (int)(c.getPreferredSize().width * zoom);
            int h = (int)(c.getPreferredSize().height* zoom);
            c.setBounds(screenPos.x + WorkSpaceScreen.SIDEBAR_WIDTH, screenPos.y, w, h);
        }

        return layout;
    }

    private static double computeK(int nodeCount) {
        return Math.sqrt((WIDTH * HEIGHT) / (double) nodeCount);
    }

}