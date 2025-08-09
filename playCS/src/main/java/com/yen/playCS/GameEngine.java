package com.yen.playCS;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameEngine {
    private final int width;
    private final int height;
    
    private Player player;
    private GameWorld gameWorld;
    private Set<Integer> pressedKeys;
    private List<Projectile> projectiles;
    
    public GameEngine(int width, int height) {
        this.width = width;
        this.height = height;
        this.pressedKeys = new HashSet<>();
        this.projectiles = new ArrayList<>();
        
        player = new Player(width / 2, height / 2);
        gameWorld = new GameWorld(width, height);
    }
    
    public void handleKeyPress(KeyEvent event) {
        pressedKeys.add(event.getKeyCode());
    }
    
    public void handleKeyRelease(KeyEvent event) {
        pressedKeys.remove(event.getKeyCode());
    }
    
    public void handleMouseMove(MouseEvent event) {
        player.updateMousePosition(event.getX(), event.getY());
    }
    
    public void handleMousePress(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1) {
            shoot();
        }
    }
    
    private void shoot() {
        Projectile bullet = player.shoot();
        if (bullet != null) {
            projectiles.add(bullet);
        }
    }
    
    public void update() {
        handleMovement();
        
        projectiles.removeIf(projectile -> {
            projectile.update();
            return projectile.isOutOfBounds(width, height) || 
                   gameWorld.checkCollision(projectile.getX(), projectile.getY());
        });
    }
    
    private void handleMovement() {
        double deltaX = 0;
        double deltaY = 0;
        
        if (pressedKeys.contains(KeyEvent.VK_W)) deltaY -= 1;
        if (pressedKeys.contains(KeyEvent.VK_S)) deltaY += 1;
        if (pressedKeys.contains(KeyEvent.VK_A)) deltaX -= 1;
        if (pressedKeys.contains(KeyEvent.VK_D)) deltaX += 1;
        
        player.move(deltaX, deltaY, gameWorld);
    }
    
    public void render(Graphics2D g2d) {
        gameWorld.render(g2d);
        player.render(g2d);
        
        for (Projectile projectile : projectiles) {
            projectile.render(g2d);
        }
        
        renderUI(g2d);
    }
    
    private void renderUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.drawString("Health: " + player.getHealth(), 10, 20);
        g2d.drawString("Ammo: " + player.getAmmo(), 10, 35);
        g2d.drawString("Position: (" + (int)player.getX() + ", " + (int)player.getY() + ")", 10, 50);
        
        g2d.setColor(Color.RED);
        int crosshairSize = 10;
        int centerX = width / 2;
        int centerY = height / 2;
        g2d.fillRect(centerX - crosshairSize/2, centerY - 1, crosshairSize, 2);
        g2d.fillRect(centerX - 1, centerY - crosshairSize/2, 2, crosshairSize);
    }
}