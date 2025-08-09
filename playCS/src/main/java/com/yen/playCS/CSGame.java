package com.yen.playCS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CSGame extends JPanel {
    
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768;
    
    private GameEngine gameEngine;
    private Timer gameTimer;

    public CSGame() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        
        gameEngine = new GameEngine(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gameEngine.handleKeyPress(e);
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                gameEngine.handleKeyRelease(e);
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                gameEngine.handleMouseMove(e);
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gameEngine.handleMousePress(e);
            }
        });
        
        gameTimer = new Timer(16, e -> {
            gameEngine.update();
            repaint();
        });
        gameTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gameEngine.render(g2d);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Counter Strike - Simple Game");
        CSGame game = new CSGame();
        
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        game.requestFocusInWindow();
    }
}
