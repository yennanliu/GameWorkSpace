package com.yen.playCS;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameWorld {
    private List<Wall> walls;
    private final int width;
    private final int height;
    
    public GameWorld(int width, int height) {
        this.width = width;
        this.height = height;
        this.walls = new ArrayList<>();
        createMap();
    }
    
    private void createMap() {
        int wallThickness = 20;
        
        walls.add(new Wall(0, 0, width, wallThickness));
        walls.add(new Wall(0, height - wallThickness, width, wallThickness));
        walls.add(new Wall(0, 0, wallThickness, height));
        walls.add(new Wall(width - wallThickness, 0, wallThickness, height));
        
        walls.add(new Wall(200, 150, 150, 20));
        walls.add(new Wall(450, 200, 20, 100));
        walls.add(new Wall(300, 400, 100, 20));
        walls.add(new Wall(600, 300, 80, 80));
        walls.add(new Wall(100, 500, 200, 20));
        walls.add(new Wall(750, 450, 20, 150));
    }
    
    public boolean checkCollision(double x, double y) {
        for (Wall wall : walls) {
            if (wall.contains(x, y)) {
                return true;
            }
        }
        return false;
    }
    
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        for (Wall wall : walls) {
            wall.render(g2d);
        }
    }
    
    private static class Wall {
        private double x, y, width, height;
        
        public Wall(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public boolean contains(double px, double py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
        
        public void render(Graphics2D g2d) {
            g2d.fillRect((int)x, (int)y, (int)width, (int)height);
        }
    }
}