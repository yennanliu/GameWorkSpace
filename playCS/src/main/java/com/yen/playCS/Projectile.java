package com.yen.playCS;

import java.awt.*;

public class Projectile {
    private double x, y;
    private double velocityX, velocityY;
    private static final double SPEED = 8.0;
    private static final double SIZE = 4.0;
    
    public Projectile(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.velocityX = Math.cos(angle) * SPEED;
        this.velocityY = Math.sin(angle) * SPEED;
    }
    
    public void update() {
        x += velocityX;
        y += velocityY;
    }
    
    public boolean isOutOfBounds(int windowWidth, int windowHeight) {
        return x < 0 || x > windowWidth || y < 0 || y > windowHeight;
    }
    
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int)(x - SIZE/2), (int)(y - SIZE/2), (int)SIZE, (int)SIZE);
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
}