package lanse.abstractt.core.displaylogic;

import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.screens.WorkSpaceScreen;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;
import java.util.*;

public class ClusterPlotMap {

    //This is a snapshot of time of my first semi working static map. Lance was making GourceMap at the time, and wanted
    // to save this for nostalgia purposes or something

    private static final int ITERATIONS = 500; //TODO - this should be at 500 by default. Make something to change this.
    private static final double WIDTH = 1920, HEIGHT = 1080;
    private static final double AREA = WIDTH * HEIGHT;
    private static final double K = Math.sqrt(AREA / 100); // nominal edge length (?)
    private static final Point2D.Double CENTER = new Point2D.Double(WIDTH/2, HEIGHT/2);
    private static final double GRAVITY = 0.05;

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

        // parent→child edges
        for (int i = 0; i < n; i++) {
            File f = new File(bubbles[i].getFilePath());
            File p = f.getParentFile();
            if (p != null) {
                Integer pi = pathToIndex.get(p.getAbsolutePath());
                if (pi != null) edges.add(new Point(pi, i));
            }
        }

        initialized = true;
    }

    //its insane how computers can do this so fast
    public static void step() {
        if (!initialized) throw new IllegalStateException("GourceMap not initialized");
        int n = nodes.length;

        // reset displacements
        for (Node v: nodes) v.disp.setLocation(0,0);

        // repulsive forces //TODO - probably needs more balancing to overcome clustering when too grouped together
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                Node vi = nodes[i], vj = nodes[j];
                double dx = vi.pos.x - vj.pos.x, dy = vi.pos.y - vj.pos.y;
                double dist = Math.max(0.01, Math.hypot(dx,dy));
                double f = (K*K)/dist;
                double fx = (dx/dist)*f, fy = (dy/dist)*f;
                vi.disp.x += fx; vi.disp.y += fy;
                vj.disp.x -= fx; vj.disp.y -= fy;
            }
        }

        // attractive forces along edges
        for (Point e: edges) {
            Node a = nodes[e.x], b = nodes[e.y];
            double dx = a.pos.x - b.pos.x, dy = a.pos.y - b.pos.y;
            double dist = Math.max(0.01, Math.hypot(dx,dy));
            double f = (dist*dist)/K;
            double fx = (dx/dist)*f, fy = (dy/dist)*f;
            a.disp.x -= fx; a.disp.y -= fy;
            b.disp.x += fx; b.disp.y += fy;
        }

        // gravity toward center
        for (Node v: nodes) {
            double dx = CENTER.x - v.pos.x, dy = CENTER.y - v.pos.y;
            v.disp.x += dx*GRAVITY;
            v.disp.y += dy*GRAVITY;
        }

        // apply displacements
        for (Node v: nodes) {
            double dlen = Math.max(0.01, Math.hypot(v.disp.x, v.disp.y));
            double scale = Math.min(dlen, 10);
            v.pos.x += (v.disp.x/dlen)*scale;
            v.pos.y += (v.disp.y/dlen)*scale;
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
}