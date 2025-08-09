package com.yen.playCS;

import java.awt.*;

public abstract class MapElement {
    protected double x, y, width, height;
    protected Color color;
    
    public MapElement(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }
    
    public boolean contains(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }
    
    public abstract void render(Graphics2D g2d);
    public abstract boolean blocksMovement();
    public abstract boolean blocksProjectiles();
    
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}