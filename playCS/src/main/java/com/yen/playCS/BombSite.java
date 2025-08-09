package com.yen.playCS;

import java.awt.*;

public class BombSite extends MapElement {
    private String siteName;
    private boolean hasBomb;
    private boolean isArmed;
    
    public BombSite(double x, double y, double width, double height, String siteName) {
        super(x, y, width, height, new Color(255, 165, 0, 100)); // Orange with transparency
        this.siteName = siteName;
        this.hasBomb = false;
        this.isArmed = false;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        // Render bomb site area
        g2d.setColor(color);
        g2d.fillRect((int)x, (int)y, (int)width, (int)height);
        
        // Border
        Color borderColor = hasBomb ? Color.RED : Color.ORANGE;
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{10, 5}, 0));
        g2d.drawRect((int)x, (int)y, (int)width, (int)height);
        
        // Site label
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (int)(x + width/2 - fm.stringWidth(siteName) / 2);
        int textY = (int)(y + height/2 + fm.getAscent() / 2);
        g2d.drawString(siteName, textX, textY);
        
        // Bomb indicator
        if (hasBomb) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String status = isArmed ? "ARMED" : "PLANTED";
            fm = g2d.getFontMetrics();
            textX = (int)(x + width/2 - fm.stringWidth(status) / 2);
            textY = (int)(y + height - 10);
            g2d.drawString(status, textX, textY);
        }
    }
    
    @Override
    public boolean blocksMovement() {
        return false;
    }
    
    @Override
    public boolean blocksProjectiles() {
        return false;
    }
    
    public String getSiteName() { return siteName; }
    public boolean hasBomb() { return hasBomb; }
    public boolean isArmed() { return isArmed; }
    
    public void plantBomb() { 
        this.hasBomb = true; 
        this.isArmed = false;
    }
    
    public void armBomb() { 
        if (hasBomb) this.isArmed = true; 
    }
    
    public void defuseBomb() { 
        this.hasBomb = false; 
        this.isArmed = false; 
    }
}