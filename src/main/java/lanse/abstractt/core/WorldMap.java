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

    public void mouseReleased() {
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
            final WorldMap map = m;

            @Override
            public void mousePressed(MouseEvent e) {
                map.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                map.mouseReleased();
            }
        });

        component.addMouseMotionListener(new MouseMotionAdapter() {
            final WorldMap map = m;

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
            double newZoom = zoom;

            int mouseX = e.getX();
            int mouseY = e.getY();

            double worldX = (mouseX / zoom) - offsetX;
            double worldY = (mouseY / zoom) - offsetY;

            // Calculate new zoom
            if (e.getPreciseWheelRotation() < 0) {
                newZoom *= zoomFactor;
            } else {
                newZoom /= zoomFactor;
            }

            //TODO - make sure this clamps properly
            newZoom = Math.max(0.001, Math.min(100.0, newZoom));

            // Only apply changes if zoom actually changed (prevents jump if already at limit)
            if (newZoom != zoom) {
                zoom = newZoom;

                // Adjust offsets to keep mouse position anchored
                offsetX = (mouseX / zoom) - worldX;
                offsetY = (mouseY / zoom) - worldY;

                //TODO - call a centralized optimized call to rescale all icons on screen. This way, bubbles that have
                // the same icon don't need to recalculate it numerous times for no reason.

                component.revalidate();
                component.repaint();
            }
        });
    }

    public Point transform(double x, double y) {
        int screenX = (int) ((x + offsetX) * zoom);
        int screenY = (int) ((y + offsetY) * zoom);
        return new Point(screenX, screenY);
    }

    public static void setCameraCoordinates(int x, int y){
        offsetX = x;
        offsetY = y;
    }

    public static double getZoom() { return instance != null ? instance.zoom : 1.0; }

    //TODO - in bubble sort or file list, have this zoom to the center of the average bubble position, with the appropriate scale.
    public static void setZoom(double zoom) {
        instance.zoom = zoom;
    }
}