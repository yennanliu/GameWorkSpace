package com.yen.playCS;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class GameEngine {
    private final int width;
    private final int height;
    
    private Player player;
    private GameWorld gameWorld;
    private Set<Integer> pressedKeys;
    private List<Projectile> projectiles;
    private List<AIEnemy> aiEnemies;
    private Random random;
    private long lastEnemySpawn;
    private static final long ENEMY_RESPAWN_DELAY = 5000; // 5 seconds
    
    public GameEngine(int width, int height) {
        this.width = width;
        this.height = height;
        this.pressedKeys = new HashSet<>();
        this.projectiles = new ArrayList<>();
        this.aiEnemies = new ArrayList<>();
        this.random = new Random();
        this.lastEnemySpawn = System.currentTimeMillis();
        
        player = new Player(width / 2, height / 2);
        gameWorld = new GameWorld(width, height);
        
        // Spawn initial AI enemies
        spawnInitialEnemies();
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
        updateAIEnemies();
        updateProjectiles();
        checkCollisions();
        handleEnemyRespawn();
    }
    
    private void updateAIEnemies() {
        for (AIEnemy enemy : aiEnemies) {
            enemy.update(player, gameWorld);
            
            // AI enemy shooting
            Projectile enemyBullet = enemy.tryShoot();
            if (enemyBullet != null) {
                projectiles.add(enemyBullet);
            }
        }
    }
    
    private void updateProjectiles() {
        projectiles.removeIf(projectile -> {
            projectile.update();
            return projectile.isOutOfBounds(width, height) || 
                   gameWorld.checkProjectileCollision(projectile.getX(), projectile.getY());
        });
    }
    
    private void checkCollisions() {
        // Check projectile hits on AI enemies
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            
            // Check hits on AI enemies
            for (AIEnemy enemy : aiEnemies) {
                if (enemy.isAlive() && enemy.contains(projectile.getX(), projectile.getY())) {
                    enemy.takeDamage(34); // Damage per hit
                    projectiles.remove(i);
                    break;
                }
            }
            
            // Check hits on player (from AI projectiles)
            if (i < projectiles.size() && player.contains(projectile.getX(), projectile.getY())) {
                player.takeDamage(25);
                projectiles.remove(i);
            }
        }
    }
    
    private void handleEnemyRespawn() {
        long currentTime = System.currentTimeMillis();
        
        // Remove dead enemies and potentially respawn them
        aiEnemies.removeIf(enemy -> {
            if (!enemy.isAlive() && currentTime - lastEnemySpawn > ENEMY_RESPAWN_DELAY) {
                return true; // Remove dead enemy
            }
            return false;
        });
        
        // Maintain 3-5 enemies on the map
        if (aiEnemies.size() < 3 && currentTime - lastEnemySpawn > ENEMY_RESPAWN_DELAY) {
            spawnRandomEnemy();
            lastEnemySpawn = currentTime;
        }
    }
    
    private void spawnInitialEnemies() {
        // Spawn 3 initial enemies with different behaviors
        spawnEnemyAtRandomSpawn(AIEnemy.AIBehavior.AGGRESSIVE);
        spawnEnemyAtRandomSpawn(AIEnemy.AIBehavior.DEFENSIVE);
        spawnEnemyAtRandomSpawn(AIEnemy.AIBehavior.PATROL);
    }
    
    private void spawnRandomEnemy() {
        AIEnemy.AIBehavior[] behaviors = AIEnemy.AIBehavior.values();
        AIEnemy.AIBehavior randomBehavior = behaviors[random.nextInt(behaviors.length)];
        spawnEnemyAtRandomSpawn(randomBehavior);
    }
    
    private void spawnEnemyAtRandomSpawn(AIEnemy.AIBehavior behavior) {
        SpawnPoint spawn = gameWorld.getRandomSpawnPoint(SpawnPoint.Team.TERRORIST);
        if (spawn != null) {
            AIEnemy enemy = new AIEnemy(spawn.getCenterX(), spawn.getCenterY(), 
                                       SpawnPoint.Team.TERRORIST, behavior);
            aiEnemies.add(enemy);
        }
    }
    
    private void handleMovement() {
        double deltaX = 0;
        double deltaY = 0;
        
        if (pressedKeys.contains(KeyEvent.VK_W)) deltaY -= 1;
        if (pressedKeys.contains(KeyEvent.VK_S)) deltaY += 1;
        if (pressedKeys.contains(KeyEvent.VK_A)) deltaX -= 1;
        if (pressedKeys.contains(KeyEvent.VK_D)) deltaX += 1;
        
        // Map switching keys
        if (pressedKeys.contains(KeyEvent.VK_1)) {
            gameWorld.switchMap("dust2");
            pressedKeys.remove(KeyEvent.VK_1); // Prevent spam
        }
        if (pressedKeys.contains(KeyEvent.VK_2)) {
            gameWorld.switchMap("office");
            pressedKeys.remove(KeyEvent.VK_2);
        }
        if (pressedKeys.contains(KeyEvent.VK_3)) {
            gameWorld.switchMap("mirage");
            pressedKeys.remove(KeyEvent.VK_3);
        }
        
        player.move(deltaX, deltaY, gameWorld);
    }
    
    public void render(Graphics2D g2d) {
        gameWorld.render(g2d);
        
        // Render AI enemies
        for (AIEnemy enemy : aiEnemies) {
            enemy.render(g2d);
        }
        
        player.render(g2d);
        
        for (Projectile projectile : projectiles) {
            projectile.render(g2d);
        }
        
        renderUI(g2d);
    }
    
    private void renderUI(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Health: " + player.getHealth(), 10, 20);
        g2d.drawString("Ammo: " + player.getAmmo(), 10, 35);
        g2d.drawString("Position: (" + (int)player.getX() + ", " + (int)player.getY() + ")", 10, 50);
        
        // AI Enemy info
        int aliveEnemies = 0;
        for (AIEnemy enemy : aiEnemies) {
            if (enemy.isAlive()) aliveEnemies++;
        }
        g2d.drawString("Enemies: " + aliveEnemies + " alive", 10, 65);
        
        // Enemy behavior legend
        g2d.setColor(Color.ORANGE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString("Enemy Types: A=Aggressive, D=Defensive, P=Patrol", width - 250, 20);
        
        // Map controls info
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Maps: [1] Dust2  [2] Office  [3] Mirage", 10, height - 20);
        
        // Game info
        g2d.setColor(Color.CYAN);
        g2d.drawString("Red enemies will hunt you! Shoot them before they shoot you!", 10, height - 40);
        
        // Player death message
        if (player.getHealth() <= 0) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            String gameOverText = "GAME OVER - You were eliminated!";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = width/2 - fm.stringWidth(gameOverText)/2;
            int textY = height/2;
            g2d.drawString(gameOverText, textX, textY);
        }
        
        // Crosshair
        if (player.getHealth() > 0) {
            g2d.setColor(Color.RED);
            int crosshairSize = 10;
            int centerX = width / 2;
            int centerY = height / 2;
            g2d.fillRect(centerX - crosshairSize/2, centerY - 1, crosshairSize, 2);
            g2d.fillRect(centerX - 1, centerY - crosshairSize/2, 2, crosshairSize);
        }
    }
}