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
        
        // Draw enemy body
        Color enemyColor = team == SpawnPoint.Team.TERRORIST ? Color.RED : Color.ORANGE;
        g2d.setColor(enemyColor);
        g2d.fillOval((int)(x - ENEMY_SIZE/2), (int)(y - ENEMY_SIZE/2), (int)ENEMY_SIZE, (int)ENEMY_SIZE);
        
        // Draw weapon direction
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        double gunLength = 20;
        double gunEndX = x + Math.cos(angle) * gunLength;
        double gunEndY = y + Math.sin(angle) * gunLength;
        g2d.drawLine((int)x, (int)y, (int)gunEndX, (int)gunEndY);
        
        // Draw health bar
        if (health < maxHealth) {
            int barWidth = (int)ENEMY_SIZE;
            int barHeight = 4;
            int barX = (int)(x - barWidth/2);
            int barY = (int)(y - ENEMY_SIZE/2 - 10);
            
            g2d.setColor(Color.RED);
            g2d.fillRect(barX, barY, barWidth, barHeight);
            
            g2d.setColor(Color.GREEN);
            int healthWidth = (int)((double)health / maxHealth * barWidth);
            g2d.fillRect(barX, barY, healthWidth, barHeight);
        }
        
        // Draw behavior indicator
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String behaviorText = behavior.name().substring(0, 1); // First letter
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (int)(x - fm.stringWidth(behaviorText) / 2);
        int textY = (int)(y + ENEMY_SIZE/2 + 15);
        g2d.drawString(behaviorText, textX, textY);
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