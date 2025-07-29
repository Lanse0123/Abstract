package lanse.abstractt.core.displaylogic;

import lanse.abstractt.core.WorldMap;
import lanse.abstractt.core.bubble.Bubble;
import lanse.abstractt.core.bubble.BubbleBridge;
import lanse.abstractt.core.screens.WorkSpaceScreen;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ProximityMap {

    //TODO - the goal of this is to make a map showing the most connected bubbles.
    // This was copied from GourceMap and edited, and still incomplete.

    private static final int ITERATIONS = 1000; //TODO - this should be at 500 by default. Make something to change this.
    private static final double WIDTH = 1920, HEIGHT = 1080;
    private static final Point2D.Double CENTER = new Point2D.Double(WIDTH/2, HEIGHT/2);

    private static final double GRAVITY = 0.05;
    private static final double MAX_DISP = 750; // Allow wider motion per step


    // Internal node representation
    private static class Node {
        Point2D.Double pos, disp = new Point2D.Double();
        Node(double x, double y) { pos = new Point2D.Double(x, y); }
    }

    private static boolean initialized = false;
    private static Node[] nodes;
    private static List<Bubble> bubblesRef;

    private static void init(Bubble[] bubbles, Container parent) {
        bubblesRef = Arrays.asList(bubbles);
        int n = bubbles.length;
        nodes = new Node[n];

        // random start positions
        Random rnd = new Random();
        Map<String,Integer> pathToIndex = new HashMap<>(n);
        for (int i = 0; i < n; i++) {
            pathToIndex.put(new File(bubbles[i].getFilePath()).getAbsolutePath(), i);
            nodes[i] = new Node(rnd.nextDouble()*WIDTH, rnd.nextDouble()*HEIGHT);
        }

        BubbleBridge.clearAll();

        //TODO - ::
        // make a list of all bubble names (NO EXTENSIONS)
        // for each bubble, get all references to other bubbles (like to_do sort)
        // getOrCreate a BubbleBridge to each pair it finds
        // repeat

        Map<String, Bubble> nameToBubble = new HashMap<>();
        for (Bubble bubble : bubbles) {
            String name = bubble.getName(); //TODO - (NO EXTENSIONS) ((make sure to remove extensions))
            nameToBubble.put(name, bubble);
        }

        // Step 2: For each bubble, read lines and find references to other bubble names
        for (Bubble bubble : bubbles) {
            File file = new File(bubble.getFilePath());

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    for (Map.Entry<String, Bubble> entry : nameToBubble.entrySet()) {
                        String targetName = entry.getKey();
                        Bubble target = entry.getValue();
                        if (target == bubble) continue; // the bubble referencing itself doesn't count
                        if (line.contains(targetName)) {
                            BubbleBridge.getOrCreate(bubble, target, parent);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Failed to read file: " + file.getPath());
            }
        }
        initialized = true;
    }

    //its insane how computers can do this so fast
    public static void step() {
        if (!initialized) throw new IllegalStateException("GourceMap not initialized");

        // reset displacements
        for (Node v : nodes) v.disp.setLocation(0, 0);

        // bubble bridge attraction / repulsion
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

        //TODO - i dont know why I shortened everything, this is ridiculous to read

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
    }

    public static Map<Bubble,Point> getLayout(Bubble[] bubbles, Component[] staticBubbles, WorldMap worldMap, Container parent) {
        if (!initialized || !bubblesRef.equals(Arrays.asList(bubbles))){
            bubbles = removeDirectoryBubbles(bubbles);
            init(bubbles, parent);
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

    public static Bubble[] removeDirectoryBubbles(Bubble[] bubbles){
        Set<Bubble> filteredBubbles = new HashSet<>(Set.of(bubbles));
        for (Bubble bubble : bubbles){
            File file = new File(bubble.getFilePath());
            if (!file.isDirectory()){
                filteredBubbles.add(bubble);
            }
        }
        return filteredBubbles.toArray(new Bubble[0]);
    }
}