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
}
