package com.yen.playCS;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VisualEffects {
    private List<Particle> particles;
    private List<MuzzleFlash> muzzleFlashes;
    private Random random;
    
    public VisualEffects() {
        particles = new ArrayList<>();
        muzzleFlashes = new ArrayList<>();
        random = new Random();
    }
    
    public void addMuzzleFlash(double x, double y, double angle) {
        muzzleFlashes.add(new MuzzleFlash(x, y, angle));
    }
    
    public void addBulletImpact(double x, double y) {
        // Create spark particles
        for (int i = 0; i < 6; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 2 + random.nextDouble() * 3;
            particles.add(new Particle(x, y, angle, speed, Color.YELLOW, 800));
        }
    }
    
    public void addBloodEffect(double x, double y) {
        // Create blood particles
        for (int i = 0; i < 4; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 1 + random.nextDouble() * 2;
            particles.add(new Particle(x, y, angle, speed, Color.RED, 1000));
        }
    }
    
    public void update() {
        // Update particles
        particles.removeIf(particle -> {
            particle.update();
            return particle.isDead();
        });
        
        // Update muzzle flashes
        muzzleFlashes.removeIf(flash -> {
            flash.update();
            return flash.isDead();
        });
    }
    
    public void render(Graphics2D g2d) {
        // Render particles
        for (Particle particle : particles) {
            particle.render(g2d);
        }
        
        // Render muzzle flashes
        for (MuzzleFlash flash : muzzleFlashes) {
            flash.render(g2d);
        }
    }
    
    private static class Particle {
        private double x, y, vx, vy;
        private Color color;
        private long startTime, duration;
        private double size;
        
        public Particle(double x, double y, double angle, double speed, Color color, long duration) {
            this.x = x;
            this.y = y;
            this.vx = Math.cos(angle) * speed;
            this.vy = Math.sin(angle) * speed;
            this.color = color;
            this.startTime = System.currentTimeMillis();
            this.duration = duration;
            this.size = 2 + Math.random() * 2;
        }
        
        public void update() {
            x += vx;
            y += vy;
            vx *= 0.95; // Friction
            vy *= 0.95;
        }
        
        public void render(Graphics2D g2d) {
            long elapsed = System.currentTimeMillis() - startTime;
            float alpha = 1.0f - (float)elapsed / duration;
            if (alpha <= 0) return;
            
            Color fadeColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255));
            g2d.setColor(fadeColor);
            g2d.fillOval((int)(x - size/2), (int)(y - size/2), (int)size, (int)size);
        }
        
        public boolean isDead() {
            return System.currentTimeMillis() - startTime > duration;
        }
    }
    
    private static class MuzzleFlash {
        private double x, y, angle;
        private long startTime;
        private static final long DURATION = 100;
        
        public MuzzleFlash(double x, double y, double angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.startTime = System.currentTimeMillis();
        }
        
        public void update() {
            // Muzzle flash doesn't move, just fades
        }
        
        public void render(Graphics2D g2d) {
            long elapsed = System.currentTimeMillis() - startTime;
            float alpha = 1.0f - (float)elapsed / DURATION;
            if (alpha <= 0) return;
            
            // Draw muzzle flash as bright yellow/orange oval
            Color flashColor = new Color(255, 255, 0, (int)(alpha * 200));
            g2d.setColor(flashColor);
            
            double flashLength = 20;
            double flashWidth = 8;
            double endX = x + Math.cos(angle) * flashLength;
            double endY = y + Math.sin(angle) * flashLength;
            
            g2d.setStroke(new BasicStroke((float)(flashWidth * alpha)));
            g2d.drawLine((int)x, (int)y, (int)endX, (int)endY);
        }
        
        public boolean isDead() {
            return System.currentTimeMillis() - startTime > DURATION;
        }
    }
}