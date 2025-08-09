package com.yen.playCS;

import java.awt.*;

public class SpawnPoint extends MapElement {
    private Team team;
    private boolean isActive;
    
    public enum Team {
        TERRORIST(Color.RED),
        COUNTER_TERRORIST(Color.BLUE),
        NEUTRAL(Color.GREEN);
        
        private final Color color;
        
        Team(Color color) {
            this.color = color;
        }
        
        public Color getColor() { return color; }
    }
    
    public SpawnPoint(double x, double y, Team team) {
        super(x, y, 30, 30, team.getColor());
        this.team = team;
        this.isActive = true;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (!isActive) return;
        
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
        g2d.fillOval((int)(x - width/2), (int)(y - height/2), (int)width, (int)height);
        
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int)(x - width/2), (int)(y - height/2), (int)width, (int)height);
        
        // Draw team indicator
        g2d.setColor(Color.WHITE);
        String teamText = team == Team.TERRORIST ? "T" : team == Team.COUNTER_TERRORIST ? "CT" : "N";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (int)(x - fm.stringWidth(teamText) / 2);
        int textY = (int)(y + fm.getAscent() / 2);
        g2d.drawString(teamText, textX, textY);
    }
    
    @Override
    public boolean blocksMovement() {
        return false;
    }
    
    @Override
    public boolean blocksProjectiles() {
        return false;
    }
    
    public Team getTeam() { return team; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    
    public double getCenterX() { return x; }
    public double getCenterY() { return y; }
}