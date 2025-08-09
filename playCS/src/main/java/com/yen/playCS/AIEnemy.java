package com.yen.playCS;

import java.awt.*;
import java.util.Random;

public class AIEnemy {
    private double x, y;
    private double targetX, targetY;
    private int health;
    private int maxHealth;
    private double angle;
    private SpawnPoint.Team team;
    private AIBehavior behavior;
    private boolean isAlive;
    private long lastShotTime;
    private double detectionRange;
    private double shootingRange;
    private double moveSpeed;
    private Random random;
    
    // AI behavior types
    public enum AIBehavior {
        AGGRESSIVE(150.0, 120.0, 2.5, 800),    // Close range, fast, quick shooting
        DEFENSIVE(200.0, 180.0, 1.5, 1500),   // Long range, slow, careful shooting  
        PATROL(120.0, 100.0, 2.0, 1200);     // Medium range, medium speed
        
        private final double detectionRange;
        private final double shootingRange;
        private final double moveSpeed;
        private final long shootCooldown;
        
        AIBehavior(double detectionRange, double shootingRange, double moveSpeed, long shootCooldown) {
            this.detectionRange = detectionRange;
            this.shootingRange = shootingRange;
            this.moveSpeed = moveSpeed;
            this.shootCooldown = shootCooldown;
        }
        
        public double getDetectionRange() { return detectionRange; }
        public double getShootingRange() { return shootingRange; }
        public double getMoveSpeed() { return moveSpeed; }
        public long getShootCooldown() { return shootCooldown; }
    }
    
    private static final double ENEMY_SIZE = 18;
    
    public AIEnemy(double x, double y, SpawnPoint.Team team, AIBehavior behavior) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.team = team;
        this.behavior = behavior;
        this.maxHealth = 100;
        this.health = maxHealth;
        this.isAlive = true;
        this.lastShotTime = 0;
        this.angle = 0;
        this.random = new Random();
        
        // Set behavior-specific properties
        this.detectionRange = behavior.getDetectionRange();
        this.shootingRange = behavior.getShootingRange();
        this.moveSpeed = behavior.getMoveSpeed();
    }
    
    public void update(Player player, GameWorld world) {
        if (!isAlive) return;
        
        double distanceToPlayer = getDistanceTo(player.getX(), player.getY());
        
        // AI decision making
        if (distanceToPlayer <= detectionRange) {
            // Player detected - aim at player
            angle = Math.atan2(player.getY() - y, player.getX() - x);
            
            if (distanceToPlayer <= shootingRange) {
                // In shooting range - shoot and adjust position
                handleCombat(player, world);
            } else {
                // Player detected but out of range - move closer
                moveTowardsTarget(player.getX(), player.getY(), world);
            }
        } else {
            // No player detected - patrol behavior
            handlePatrol(world);
        }
        
        // Update position based on movement
        moveTowardsCurrentTarget(world);
    }
    
    private void handleCombat(Player player, GameWorld world) {
        switch (behavior) {
            case AGGRESSIVE:
                // Move closer and shoot frequently
                if (getDistanceTo(player.getX(), player.getY()) > 60) {
                    moveTowardsTarget(player.getX(), player.getY(), world);
                }
                break;
                
            case DEFENSIVE:
                // Keep distance and find cover
                if (getDistanceTo(player.getX(), player.getY()) < 100) {
                    // Move away from player
                    double awayX = x - (player.getX() - x) * 0.5;
                    double awayY = y - (player.getY() - y) * 0.5;
                    moveTowardsTarget(awayX, awayY, world);
                }
                break;
                
            case PATROL:
                // Moderate approach
                if (getDistanceTo(player.getX(), player.getY()) > 80) {
                    moveTowardsTarget(player.getX(), player.getY(), world);
                }
                break;
        }
    }
    
    private void handlePatrol(GameWorld world) {
        // Simple patrol - move to random nearby positions
        if (getDistanceTo(targetX, targetY) < 20) {
            // Reached target, pick new one
            targetX = x + (random.nextDouble() - 0.5) * 200;
            targetY = y + (random.nextDouble() - 0.5) * 200;
            
            // Keep within reasonable bounds
            targetX = Math.max(50, Math.min(targetX, 950));
            targetY = Math.max(50, Math.min(targetY, 700));
        }
    }
    
    private void moveTowardsTarget(double tx, double ty, GameWorld world) {
        this.targetX = tx;
        this.targetY = ty;
    }
    
    private void moveTowardsCurrentTarget(GameWorld world) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 5) {
            double moveX = (dx / distance) * moveSpeed;
            double moveY = (dy / distance) * moveSpeed;
            
            double newX = x + moveX;
            double newY = y + moveY;
            
            // Check collision with walls
            if (!world.checkCollision(newX, newY)) {
                x = newX;
                y = newY;
            } else {
                // Try moving just in X or Y direction
                if (!world.checkCollision(x + moveX, y)) {
                    x += moveX;
                } else if (!world.checkCollision(x, y + moveY)) {
                    y += moveY;
                } else {
                    // Completely blocked, pick new target
                    targetX = x + (random.nextDouble() - 0.5) * 100;
                    targetY = y + (random.nextDouble() - 0.5) * 100;
                }
            }
        }
    }
    
    public Projectile tryShoot() {
        if (!isAlive) return null;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= behavior.getShootCooldown()) {
            lastShotTime = currentTime;
            
            // Add some inaccuracy to AI shooting
            double accuracy = 0.9; // 90% accuracy
            double spread = (1.0 - accuracy) * (random.nextDouble() - 0.5);
            double shootAngle = angle + spread;
            
            double bulletStartX = x + Math.cos(shootAngle) * (ENEMY_SIZE / 2 + 5);
            double bulletStartY = y + Math.sin(shootAngle) * (ENEMY_SIZE / 2 + 5);
            
            return new Projectile(bulletStartX, bulletStartY, shootAngle);
        }
        return null;
    }
    
    public void takeDamage(int damage) {
        if (!isAlive) return;
        
        health -= damage;
        if (health <= 0) {
            health = 0;
            isAlive = false;
        }
    }
    
    public void respawn(SpawnPoint spawnPoint) {
        if (spawnPoint != null && spawnPoint.getTeam() == team) {
            x = spawnPoint.getCenterX();
            y = spawnPoint.getCenterY();
            health = maxHealth;
            isAlive = true;
            targetX = x;
            targetY = y;
        }
    }
    
    private double getDistanceTo(double tx, double ty) {
        double dx = tx - x;
        double dy = ty - y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public void render(Graphics2D g2d) {
        if (!isAlive) return;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Enemy body with behavior-specific colors
        Color bodyColor, highlightColor;
        switch (behavior) {
            case AGGRESSIVE:
                bodyColor = new Color(180, 0, 0);      // Dark red
                highlightColor = new Color(220, 50, 50); // Bright red
                break;
            case DEFENSIVE:
                bodyColor = new Color(140, 70, 0);      // Dark orange
                highlightColor = new Color(200, 120, 50); // Bright orange
                break;
            case PATROL:
                bodyColor = new Color(120, 0, 120);     // Purple
                highlightColor = new Color(180, 50, 180); // Bright purple
                break;
            default:
                bodyColor = Color.RED;
                highlightColor = Color.PINK;
        }
        
        // Create gradient for enemy body
        GradientPaint bodyGradient = new GradientPaint(
            (float)(x - ENEMY_SIZE/2), (float)(y - ENEMY_SIZE/2), highlightColor,
            (float)(x + ENEMY_SIZE/2), (float)(y + ENEMY_SIZE/2), bodyColor
        );
        g2d.setPaint(bodyGradient);
        g2d.fillOval((int)(x - ENEMY_SIZE/2), (int)(y - ENEMY_SIZE/2), (int)ENEMY_SIZE, (int)ENEMY_SIZE);
        
        // Body border
        g2d.setColor(bodyColor.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int)(x - ENEMY_SIZE/2), (int)(y - ENEMY_SIZE/2), (int)ENEMY_SIZE, (int)ENEMY_SIZE);
        
        // Weapon (AK-47 style for terrorists)
        g2d.setColor(new Color(40, 40, 40)); // Dark gun metal
        g2d.setStroke(new BasicStroke(3));
        double gunLength = 22;
        double gunEndX = x + Math.cos(angle) * gunLength;
        double gunEndY = y + Math.sin(angle) * gunLength;
        g2d.drawLine((int)x, (int)y, (int)gunEndX, (int)gunEndY);
        
        // Gun details
        g2d.setColor(new Color(60, 60, 60));
        g2d.setStroke(new BasicStroke(1));
        double gunDetailX = x + Math.cos(angle) * 15;
        double gunDetailY = y + Math.sin(angle) * 15;
        g2d.drawLine((int)gunDetailX, (int)gunDetailY, (int)gunEndX, (int)gunEndY);
        
        // Enemy sight indicator
        g2d.setColor(Color.YELLOW);
        double sightX = x + Math.cos(angle) * 8;
        double sightY = y + Math.sin(angle) * 8;
        g2d.fillOval((int)(sightX - 1), (int)(sightY - 1), 2, 2);
        
        // Draw enhanced health bar when damaged
        if (health < maxHealth) {
            drawEnemyHealthBar(g2d);
        }
        
        // Behavior indicator with modern styling
        drawBehaviorIndicator(g2d);
        
        // Detection range indicator (when aggressive and close to player)
        if (behavior == AIBehavior.AGGRESSIVE) {
            drawDetectionRange(g2d);
        }
    }
    
    private void drawEnemyHealthBar(Graphics2D g2d) {
        int barWidth = (int)ENEMY_SIZE + 4;
        int barHeight = 5;
        int barX = (int)(x - barWidth/2);
        int barY = (int)(y - ENEMY_SIZE/2 - 12);
        
        // Background
        g2d.setColor(new Color(60, 60, 60));
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 3, 3);
        
        // Health fill with gradient
        double healthPercent = (double)health / maxHealth;
        Color healthColor = healthPercent > 0.6 ? new Color(0, 200, 0) : 
                           healthPercent > 0.3 ? new Color(255, 165, 0) : new Color(220, 50, 50);
        
        g2d.setColor(healthColor);
        int healthWidth = (int)(barWidth * healthPercent);
        if (healthWidth > 0) {
            g2d.fillRoundRect(barX, barY, healthWidth, barHeight, 3, 3);
        }
        
        // Border
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(barX, barY, barWidth, barHeight, 3, 3);
    }
    
    private void drawBehaviorIndicator(Graphics2D g2d) {
        // Background circle for behavior indicator
        int indicatorSize = 16;
        int indicatorX = (int)(x - indicatorSize/2);
        int indicatorY = (int)(y + ENEMY_SIZE/2 + 8);
        
        // Behavior-specific background color
        Color bgColor;
        switch (behavior) {
            case AGGRESSIVE: bgColor = new Color(255, 100, 100, 180); break;
            case DEFENSIVE: bgColor = new Color(255, 165, 0, 180); break;
            case PATROL: bgColor = new Color(200, 100, 255, 180); break;
            default: bgColor = new Color(150, 150, 150, 180);
        }
        
        g2d.setColor(bgColor);
        g2d.fillOval(indicatorX, indicatorY, indicatorSize, indicatorSize);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(indicatorX, indicatorY, indicatorSize, indicatorSize);
        
        // Behavior letter
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String behaviorText = behavior.name().substring(0, 1);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = indicatorX + (indicatorSize - fm.stringWidth(behaviorText)) / 2;
        int textY = indicatorY + indicatorSize - 4;
        g2d.drawString(behaviorText, textX, textY);
    }
    
    private void drawDetectionRange(Graphics2D g2d) {
        // Only show when aggressive and actively hunting
        g2d.setColor(new Color(255, 0, 0, 30));
        g2d.fillOval((int)(x - detectionRange/4), (int)(y - detectionRange/4), 
                     (int)(detectionRange/2), (int)(detectionRange/2));
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public int getHealth() { return health; }
    public boolean isAlive() { return isAlive; }
    public SpawnPoint.Team getTeam() { return team; }
    public AIBehavior getBehavior() { return behavior; }
    public double getSize() { return ENEMY_SIZE; }
    
    // Check if point is within enemy bounds
    public boolean contains(double px, double py) {
        double dx = px - x;
        double dy = py - y;
        return Math.sqrt(dx * dx + dy * dy) <= ENEMY_SIZE / 2;
    }
}