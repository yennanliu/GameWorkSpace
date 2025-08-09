package com.yen.playCS;

import java.awt.*;

public class Wall extends MapElement {
    private WallType type;
    
    public enum WallType {
        CONCRETE(Color.GRAY, true, true),
        BRICK(new Color(139, 69, 19), true, true),
        WOOD(new Color(160, 82, 45), true, true),
        METAL(Color.DARK_GRAY, true, true),
        COVER(new Color(101, 67, 33), true, false); // Blocks movement but not bullets
        
        private final Color color;
        private final boolean blocksMovement;
        private final boolean blocksProjectiles;
        
        WallType(Color color, boolean blocksMovement, boolean blocksProjectiles) {
            this.color = color;
            this.blocksMovement = blocksMovement;
            this.blocksProjectiles = blocksProjectiles;
        }
        
        public Color getColor() { return color; }
        public boolean blocksMovement() { return blocksMovement; }
        public boolean blocksProjectiles() { return blocksProjectiles; }
    }
    
    public Wall(double x, double y, double width, double height, WallType type) {
        super(x, y, width, height, type.getColor());
        this.type = type;
    }
    
    public Wall(double x, double y, double width, double height) {
        this(x, y, width, height, WallType.CONCRETE);
    }
    
    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillRect((int)x, (int)y, (int)width, (int)height);
        
        // Add border for better visibility
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect((int)x, (int)y, (int)width, (int)height);
    }
    
    @Override
    public boolean blocksMovement() {
        return type.blocksMovement();
    }
    
    @Override
    public boolean blocksProjectiles() {
        return type.blocksProjectiles();
    }
    
    public WallType getType() {
        return type;
    }
}