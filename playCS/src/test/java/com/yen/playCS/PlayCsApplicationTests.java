package com.yen.playCS;

import org.junit.Test;
import static org.junit.Assert.*;

public class PlayCsApplicationTests {

    @Test
    public void testPlayerCreation() {
        Player player = new Player(100, 100);
        assertEquals(100.0, player.getX(), 0.1);
        assertEquals(100.0, player.getY(), 0.1);
        assertEquals(100, player.getHealth());
        assertEquals(30, player.getAmmo());
    }
    
    @Test
    public void testProjectileCreation() {
        Projectile projectile = new Projectile(100, 100, 0);
        assertEquals(100.0, projectile.getX(), 0.1);
        assertEquals(100.0, projectile.getY(), 0.1);
    }
    
    @Test
    public void testGameWorldCollision() {
        GameWorld world = new GameWorld(800, 600);
        assertTrue("Should collide with boundary walls", world.checkCollision(5, 5));
        assertFalse("Should not collide in open area", world.checkCollision(400, 300));
    }
    
    @Test
    public void testMapSwitching() {
        GameWorld world = new GameWorld(800, 600, "dust2");
        assertEquals("dust2", world.getMapName());
        
        world.switchMap("office");
        assertEquals("office", world.getMapName());
        
        world.switchMap("mirage");
        assertEquals("mirage", world.getMapName());
    }
    
    @Test
    public void testSpawnPoints() {
        GameWorld world = new GameWorld(800, 600, "dust2");
        assertFalse("Should have spawn points", world.getSpawnPoints().isEmpty());
        assertTrue("Should have bomb sites", world.getBombSites().size() >= 2);
    }
    
    @Test
    public void testAIEnemyCreation() {
        AIEnemy enemy = new AIEnemy(100, 100, SpawnPoint.Team.TERRORIST, AIEnemy.AIBehavior.AGGRESSIVE);
        assertEquals(100.0, enemy.getX(), 0.1);
        assertEquals(100.0, enemy.getY(), 0.1);
        assertTrue("Enemy should be alive initially", enemy.isAlive());
        assertEquals(SpawnPoint.Team.TERRORIST, enemy.getTeam());
    }
    
    @Test
    public void testAIEnemyDamage() {
        AIEnemy enemy = new AIEnemy(100, 100, SpawnPoint.Team.TERRORIST, AIEnemy.AIBehavior.PATROL);
        assertTrue("Enemy should be alive", enemy.isAlive());
        
        enemy.takeDamage(50);
        assertTrue("Enemy should still be alive", enemy.isAlive());
        
        enemy.takeDamage(60);
        assertFalse("Enemy should be dead", enemy.isAlive());
    }
}
