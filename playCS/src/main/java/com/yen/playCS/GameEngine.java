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
    private VisualEffects visualEffects;
    private Random random;
    private long lastEnemySpawn;
    private static final long ENEMY_RESPAWN_DELAY = 5000; // 5 seconds
    
    public GameEngine(int width, int height) {
        this.width = width;
        this.height = height;
        this.pressedKeys = new HashSet<>();
        this.projectiles = new ArrayList<>();
        this.aiEnemies = new ArrayList<>();
        this.visualEffects = new VisualEffects();
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
            // Add muzzle flash effect
            visualEffects.addMuzzleFlash(player.getX(), player.getY(), player.getAngle());
        }
    }
    
    public void update() {
        handleMovement();
        updateAIEnemies();
        updateProjectiles();
        checkCollisions();
        handleEnemyRespawn();
        visualEffects.update();
    }
    
    private void updateAIEnemies() {
        for (AIEnemy enemy : aiEnemies) {
            enemy.update(player, gameWorld);
            
            // AI enemy shooting
            Projectile enemyBullet = enemy.tryShoot();
            if (enemyBullet != null) {
                projectiles.add(enemyBullet);
                // Add muzzle flash for AI
                visualEffects.addMuzzleFlash(enemy.getX(), enemy.getY(), 
                                           Math.atan2(player.getY() - enemy.getY(), 
                                                    player.getX() - enemy.getX()));
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
                    visualEffects.addBloodEffect(projectile.getX(), projectile.getY());
                    visualEffects.addBulletImpact(projectile.getX(), projectile.getY());
                    projectiles.remove(i);
                    break;
                }
            }
            
            // Check hits on player (from AI projectiles)
            if (i < projectiles.size() && player.contains(projectile.getX(), projectile.getY())) {
                player.takeDamage(25);
                visualEffects.addBloodEffect(projectile.getX(), projectile.getY());
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
        // Draw modern game background
        ModernUI.drawGameBackground(g2d, width, height, gameWorld.getMapName());
        
        // Render game world elements
        gameWorld.render(g2d);
        
        // Render AI enemies
        for (AIEnemy enemy : aiEnemies) {
            enemy.render(g2d);
        }
        
        // Render player
        player.render(g2d);
        
        // Render projectiles
        for (Projectile projectile : projectiles) {
            projectile.render(g2d);
        }
        
        // Render visual effects
        visualEffects.render(g2d);
        
        // Render modern UI
        renderModernUI(g2d);
    }
    
    private void renderModernUI(Graphics2D g2d) {
        // Count alive enemies
        int aliveEnemies = 0;
        for (AIEnemy enemy : aiEnemies) {
            if (enemy.isAlive()) aliveEnemies++;
        }
        
        // Use the modern UI system
        ModernUI.drawModernHUD(g2d, player, aliveEnemies, gameWorld.getMapName(), width, height);
    }
}