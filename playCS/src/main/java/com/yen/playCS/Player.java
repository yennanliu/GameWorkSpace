package com.yen.playCS;

import java.awt.*;

public class Player {
    private double x, y;
    private double mouseX, mouseY;
    private int health;
    private int ammo;
    private double angle;
    private static final double MOVE_SPEED = 3.0;
    private static final double PLAYER_SIZE = 20;
    
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.health = 100;
        this.ammo = 30;
        this.angle = 0;
    }
    
    public void move(double deltaX, double deltaY, GameWorld world) {
        if (deltaX == 0 && deltaY == 0) return;
        
        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        deltaX = (deltaX / length) * MOVE_SPEED;
        deltaY = (deltaY / length) * MOVE_SPEED;
        
        double newX = x + deltaX;
        double newY = y + deltaY;
        
        if (!world.checkCollision(newX, newY)) {
            x = newX;
            y = newY;
        }
    }
    
    public void updateMousePosition(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        
        double dx = mouseX - x;
        double dy = mouseY - y;
        this.angle = Math.atan2(dy, dx);
    }
    
    public Projectile shoot() {
        if (ammo <= 0) return null;
        
        ammo--;
        
        double bulletStartX = x + Math.cos(angle) * (PLAYER_SIZE / 2 + 5);
        double bulletStartY = y + Math.sin(angle) * (PLAYER_SIZE / 2 + 5);
        
        return new Projectile(bulletStartX, bulletStartY, angle);
    }
    
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);
        g2d.fillOval((int)(x - PLAYER_SIZE/2), (int)(y - PLAYER_SIZE/2), (int)PLAYER_SIZE, (int)PLAYER_SIZE);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        double gunLength = 25;
        double gunEndX = x + Math.cos(angle) * gunLength;
        double gunEndY = y + Math.sin(angle) * gunLength;
        g2d.drawLine((int)x, (int)y, (int)gunEndX, (int)gunEndY);
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public int getHealth() { return health; }
    public int getAmmo() { return ammo; }
    public double getSize() { return PLAYER_SIZE; }
}