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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (health <= 0) return; // Don't render if dead
        
        // Player body (Counter-Terrorist blue)
        Color bodyColor = new Color(0, 100, 200);
        Color bodyHighlight = new Color(50, 150, 255);
        
        // Create gradient for body
        GradientPaint bodyGradient = new GradientPaint(
            (float)(x - PLAYER_SIZE/2), (float)(y - PLAYER_SIZE/2), bodyHighlight,
            (float)(x + PLAYER_SIZE/2), (float)(y + PLAYER_SIZE/2), bodyColor
        );
        g2d.setPaint(bodyGradient);
        g2d.fillOval((int)(x - PLAYER_SIZE/2), (int)(y - PLAYER_SIZE/2), (int)PLAYER_SIZE, (int)PLAYER_SIZE);
        
        // Body border
        g2d.setColor(new Color(0, 50, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int)(x - PLAYER_SIZE/2), (int)(y - PLAYER_SIZE/2), (int)PLAYER_SIZE, (int)PLAYER_SIZE);
        
        // Weapon
        g2d.setColor(new Color(60, 60, 60)); // Gun metal color
        g2d.setStroke(new BasicStroke(4));
        double gunLength = 28;
        double gunEndX = x + Math.cos(angle) * gunLength;
        double gunEndY = y + Math.sin(angle) * gunLength;
        g2d.drawLine((int)x, (int)y, (int)gunEndX, (int)gunEndY);
        
        // Gun barrel
        g2d.setColor(new Color(40, 40, 40));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine((int)(x + Math.cos(angle) * 20), (int)(y + Math.sin(angle) * 20), 
                     (int)gunEndX, (int)gunEndY);
        
        // Player direction indicator (small arrow)
        g2d.setColor(Color.WHITE);
        double arrowSize = 6;
        double arrowX = x + Math.cos(angle) * 12;
        double arrowY = y + Math.sin(angle) * 12;
        g2d.fillOval((int)(arrowX - 2), (int)(arrowY - 2), 4, 4);
        
        // Health indicator (only show when damaged)
        if (health < 100) {
            drawHealthBar(g2d);
        }
    }
    
    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = (int)PLAYER_SIZE;
        int barHeight = 4;
        int barX = (int)(x - barWidth/2);
        int barY = (int)(y - PLAYER_SIZE/2 - 8);
        
        // Background
        g2d.setColor(new Color(60, 60, 60));
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Health fill
        double healthPercent = (double)health / 100.0;
        Color healthColor = healthPercent > 0.5 ? Color.GREEN : 
                           healthPercent > 0.2 ? Color.YELLOW : Color.RED;
        
        g2d.setColor(healthColor);
        int healthWidth = (int)(barWidth * healthPercent);
        g2d.fillRect(barX, barY, healthWidth, barHeight);
        
        // Border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    
    public boolean contains(double px, double py) {
        double dx = px - x;
        double dy = py - y;
        return Math.sqrt(dx * dx + dy * dy) <= PLAYER_SIZE / 2;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public int getHealth() { return health; }
    public int getAmmo() { return ammo; }
    public double getSize() { return PLAYER_SIZE; }
    public double getAngle() { return angle; }
}