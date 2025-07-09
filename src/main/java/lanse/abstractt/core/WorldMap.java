package lanse.abstractt.core;

import java.awt.*;
import java.awt.event.*;

public class WorldMap {

    private static double offsetX = 0;
    private static double offsetY = 0;
    private double zoom = 1.0;
    private boolean dragging = false;
    private int lastMouseX, lastMouseY;

    private static WorldMap instance;

    public WorldMap() {
        instance = this;
    }

    public void initializeListeners(Component component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragging = true;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });

        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    int dx = e.getX() - lastMouseX;
                    int dy = e.getY() - lastMouseY;

                    offsetX += dx / zoom;
                    offsetY += dy / zoom;

                    lastMouseX = e.getX();
                    lastMouseY = e.getY();

                    component.revalidate();
                    component.repaint();
                }
            }
        });

        //Zoomer
        component.addMouseWheelListener(e -> {
            double zoomFactor = 1.1;
            //double oldZoom = zoom;

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

    public Point transform(double x, double y) {
        int screenX = (int) ((x + offsetX) * zoom);
        int screenY = (int) ((y + offsetY) * zoom);
        return new Point(screenX, screenY);
    }

    //TODO - I somehow ended up with 2 getZooms, which is kinda annoying
    public double getZoom() {
        return zoom;
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
    public static double getZoomStatic() { return instance != null ? instance.getZoom() : 1.0; }
}