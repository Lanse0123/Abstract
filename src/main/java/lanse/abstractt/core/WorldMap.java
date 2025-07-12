package lanse.abstractt.core;

import java.awt.*;
import java.awt.event.*;

public class WorldMap {

    //TODO - i dont like how half of this is static and half of it isn't...
    private static double offsetX = 0;
    private static double offsetY = 0;
    private double zoom = 1.0;
    private boolean dragging = false;
    private int lastMouseX, lastMouseY;
    private static WorldMap instance;

    public WorldMap() {
        instance = this;
    }

    public void mousePressed(MouseEvent e) {
        dragging = true;
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging) {
            int dx = e.getX() - lastMouseX;
            int dy = e.getY() - lastMouseY;

            offsetX += dx / zoom;
            offsetY += dy / zoom;

            lastMouseX = e.getX();
            lastMouseY = e.getY();
        }
    }

    public void initializeListeners(Component component) {
        WorldMap m = this;
        component.addMouseListener(new MouseAdapter() {
            WorldMap map = m;

            @Override
            public void mousePressed(MouseEvent e) {
                map.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                map.mouseReleased(e);
            }
        });

        component.addMouseMotionListener(new MouseMotionAdapter() {
            WorldMap map = m;
            @Override
            public void mouseDragged(MouseEvent e) {
                map.mouseDragged(e);

                component.revalidate();
                component.repaint();
            }
        });

        //Zoomer
        component.addMouseWheelListener(e -> {
            double zoomFactor = 1.1;
            //double oldZoom = zoom;
            //TODO - i might want to make use of oldZoom to add a limit to how far in and out you can zoom.
            // Max is 100, Min is 0.001

            int mouseX = e.getX();
            int mouseY = e.getY();

            double worldX = (mouseX / zoom) - offsetX;
            double worldY = (mouseY / zoom) - offsetY;

            // Apply zoom
            if (e.getPreciseWheelRotation() < 0) {
                zoom *= zoomFactor;
            } else {
                zoom /= zoomFactor;
            }

            // this makes it zoom centered on the mouse instead of an arbitrary point
            offsetX = (mouseX / zoom) - worldX;
            offsetY = (mouseY / zoom) - worldY;

            component.revalidate();
            component.repaint();
        });
    }

    //TODO - this would be nice to have in some way but it doesnt work yet. Or just having a way to get all components in use from anywhere.
//    public static void updateScreen(){
//        Container parent = getParent();
//        if (parent == null) return;
//
//        for (Component comp : parent.getComponents()) {
//            if (comp instanceof Bubble) {
//                if (comp instanceof FunctionBubble functionBubble){
//                    Storage.saveFunctionBubble(functionBubble);
//                } else {
//                    Storage.save((Bubble) comp);
//                }
//                parent.remove(comp);
//            }
//        }
//    }

    public Point transform(double x, double y) {
        int screenX = (int) ((x + offsetX) * zoom);
        int screenY = (int) ((y + offsetY) * zoom);
        return new Point(screenX, screenY);
    }

    public double getX() {
        return offsetX;
    }

    public double getY() {
        return offsetY;
    }

    public static void setCameraCoordinates(int x, int y){
        offsetX = x;
        offsetY = y;
    }

    //TODO - I somehow ended up with 2 getZooms, which is kinda annoying
    public double getZoom() { return zoom; }
    public static double getZoomStatic() { return instance != null ? instance.zoom : 1.0; }
    public static void setZoom(double zoom) { instance.zoom = zoom; }
}