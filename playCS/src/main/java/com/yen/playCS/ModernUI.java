package com.yen.playCS;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernUI {
    private static final Color DARK_BACKGROUND = new Color(20, 25, 30);
    private static final Color UI_BACKGROUND = new Color(40, 45, 50, 200);
    private static final Color ACCENT_COLOR = new Color(0, 150, 255);
    private static final Color SUCCESS_COLOR = new Color(0, 200, 100);
    private static final Color WARNING_COLOR = new Color(255, 165, 0);
    private static final Color DANGER_COLOR = new Color(220, 50, 50);
    
    public static void drawModernHUD(Graphics2D g2d, Player player, int enemyCount, String mapName, int width, int height) {
        // Enable anti-aliasing for smoother UI
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw main HUD panel (top-left)
        drawHUDPanel(g2d, 10, 10, 200, 100, player, enemyCount);
        
        // Draw map info panel (top-right)
        drawMapPanel(g2d, width - 160, 10, 150, 60, mapName);
        
        // Draw crosshair
        if (player.getHealth() > 0) {
            drawModernCrosshair(g2d, width / 2, height / 2);
        }
        
        // Draw game over screen if player is dead
        if (player.getHealth() <= 0) {
            drawGameOverScreen(g2d, width, height);
        }
        
        // Draw instructions panel (bottom)
        drawInstructionsPanel(g2d, 10, height - 80, width - 20, 70);
    }
    
    private static void drawHUDPanel(Graphics2D g2d, int x, int y, int width, int height, Player player, int enemyCount) {
        // Panel background
        g2d.setColor(UI_BACKGROUND);
        g2d.fillRoundRect(x, y, width, height, 10, 10);
        
        // Border
        g2d.setColor(ACCENT_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width, height, 10, 10);
        
        // Health bar
        drawProgressBar(g2d, x + 10, y + 15, width - 20, 20, "HEALTH", 
                       player.getHealth(), 100, SUCCESS_COLOR, DANGER_COLOR);
        
        // Ammo bar
        drawProgressBar(g2d, x + 10, y + 45, width - 20, 15, "AMMO", 
                       player.getAmmo(), 30, WARNING_COLOR, DANGER_COLOR);
        
        // Enemy counter
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("ENEMIES: " + enemyCount, x + 10, y + 80);
        
        // Position
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString("X: " + (int)player.getX() + " Y: " + (int)player.getY(), x + 10, y + 95);
    }
    
    private static void drawProgressBar(Graphics2D g2d, int x, int y, int width, int height, 
                                       String label, int current, int max, Color goodColor, Color badColor) {
        // Background
        g2d.setColor(new Color(60, 60, 60));
        g2d.fillRoundRect(x, y, width, height, 5, 5);
        
        // Progress fill
        double percentage = (double)current / max;
        Color fillColor = percentage > 0.5 ? goodColor : 
                         percentage > 0.2 ? WARNING_COLOR : badColor;
        
        g2d.setColor(fillColor);
        int fillWidth = (int)(width * percentage);
        if (fillWidth > 0) {
            g2d.fillRoundRect(x, y, fillWidth, height, 5, 5);
        }
        
        // Border
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x, y, width, height, 5, 5);
        
        // Text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        String text = label + ": " + current + "/" + max;
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + height - 3;
        g2d.drawString(text, textX, textY);
    }
    
    private static void drawMapPanel(Graphics2D g2d, int x, int y, int width, int height, String mapName) {
        // Panel background
        g2d.setColor(UI_BACKGROUND);
        g2d.fillRoundRect(x, y, width, height, 10, 10);
        
        // Border
        g2d.setColor(ACCENT_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, width, height, 10, 10);
        
        // Map name
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "MAP: " + mapName.toUpperCase();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        g2d.drawString(text, textX, y + 20);
        
        // Map switching hint
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(new Color(200, 200, 200));
        text = "Press 1-3 to switch";
        textX = x + (width - fm.stringWidth(text)) / 2;
        g2d.drawString(text, textX, y + 40);
    }
    
    private static void drawModernCrosshair(Graphics2D g2d, int centerX, int centerY) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Outer ring
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(centerX - 15, centerY - 15, 30, 30);
        
        // Center dot
        g2d.setColor(Color.RED);
        g2d.fillOval(centerX - 2, centerY - 2, 4, 4);
        
        // Cross lines
        g2d.setColor(new Color(255, 0, 0, 180));
        g2d.setStroke(new BasicStroke(2));
        
        // Horizontal line (with gap in middle)
        g2d.drawLine(centerX - 12, centerY, centerX - 5, centerY);
        g2d.drawLine(centerX + 5, centerY, centerX + 12, centerY);
        
        // Vertical line (with gap in middle)  
        g2d.drawLine(centerX, centerY - 12, centerX, centerY - 5);
        g2d.drawLine(centerX, centerY + 5, centerX, centerY + 12);
    }
    
    private static void drawGameOverScreen(Graphics2D g2d, int width, int height) {
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, width, height);
        
        // Game over panel
        int panelWidth = 400;
        int panelHeight = 200;
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;
        
        g2d.setColor(DARK_BACKGROUND);
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        g2d.setColor(DANGER_COLOR);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        // Game over text
        g2d.setColor(DANGER_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "ELIMINATED";
        int textX = panelX + (panelWidth - fm.stringWidth(text)) / 2;
        g2d.drawString(text, textX, panelY + 80);
        
        // Subtitle
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        fm = g2d.getFontMetrics();
        text = "You were taken down by hostile forces";
        textX = panelX + (panelWidth - fm.stringWidth(text)) / 2;
        g2d.drawString(text, textX, panelY + 120);
        
        // Restart hint
        g2d.setColor(ACCENT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        fm = g2d.getFontMetrics();
        text = "Restart the game to try again";
        textX = panelX + (panelWidth - fm.stringWidth(text)) / 2;
        g2d.drawString(text, textX, panelY + 150);
    }
    
    private static void drawInstructionsPanel(Graphics2D g2d, int x, int y, int width, int height) {
        // Panel background
        g2d.setColor(new Color(UI_BACKGROUND.getRed(), UI_BACKGROUND.getGreen(), UI_BACKGROUND.getBlue(), 150));
        g2d.fillRoundRect(x, y, width, height, 10, 10);
        
        // Instructions text
        g2d.setColor(new Color(220, 220, 220));
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        String[] instructions = {
            "🎮 WASD: Move  |  🖱️ Mouse: Aim  |  🔫 Left Click: Shoot  |  🗺️ 1-3: Switch Maps",
            "🤖 Enemy Types: A=Aggressive, D=Defensive, P=Patrol  |  💥 Eliminate all hostiles to survive!"
        };
        
        for (int i = 0; i < instructions.length; i++) {
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (width - fm.stringWidth(instructions[i])) / 2;
            g2d.drawString(instructions[i], textX, y + 25 + i * 20);
        }
    }
    
    // Background patterns and textures
    public static void drawGameBackground(Graphics2D g2d, int width, int height, String mapName) {
        // Base background
        drawTexturedBackground(g2d, width, height, mapName);
        
        // Add subtle grid pattern
        drawGridPattern(g2d, width, height);
    }
    
    private static void drawTexturedBackground(Graphics2D g2d, int width, int height, String mapName) {
        Color baseColor;
        Color accentColor;
        
        switch (mapName.toLowerCase()) {
            case "dust2":
                baseColor = new Color(139, 119, 101); // Desert sand
                accentColor = new Color(160, 140, 120);
                break;
            case "office":
                baseColor = new Color(240, 240, 245); // Office white
                accentColor = new Color(220, 220, 230);
                break;
            case "mirage":
                baseColor = new Color(205, 180, 140); // Mediterranean stone
                accentColor = new Color(225, 200, 160);
                break;
            default:
                baseColor = new Color(60, 80, 60); // Default green
                accentColor = new Color(80, 100, 80);
                break;
        }
        
        // Gradient background
        GradientPaint gradient = new GradientPaint(0, 0, baseColor, width, height, accentColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
    }
    
    private static void drawGridPattern(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(255, 255, 255, 15)); // Very subtle white grid
        g2d.setStroke(new BasicStroke(1));
        
        int gridSize = 50;
        
        // Vertical lines
        for (int x = 0; x < width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
        }
        
        // Horizontal lines
        for (int y = 0; y < height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
        }
    }
}