package com.yen.playCS;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld {
    private List<MapElement> mapElements;
    private List<SpawnPoint> spawnPoints;
    private List<BombSite> bombSites;
    private final int width;
    private final int height;
    private String mapName;
    
    public GameWorld(int width, int height) {
        this.width = width;
        this.height = height;
        this.mapElements = new ArrayList<>();
        this.spawnPoints = new ArrayList<>();
        this.bombSites = new ArrayList<>();
        createMap("dust2"); // Default map
    }
    
    public GameWorld(int width, int height, String mapName) {
        this(width, height);
        createMap(mapName);
    }
    
    private void createMap(String mapName) {
        this.mapName = mapName;
        mapElements.clear();
        spawnPoints.clear();
        bombSites.clear();
        
        switch (mapName.toLowerCase()) {
            case "dust2":
                createDust2Map();
                break;
            case "office":
                createOfficeMap();
                break;
            case "mirage":
                createMirageMap();
                break;
            default:
                createDefaultMap();
                break;
        }
    }
    
    private void createDust2Map() {
        int wallThickness = 20;
        
        // Boundary walls
        addWall(0, 0, width, wallThickness, Wall.WallType.CONCRETE);
        addWall(0, height - wallThickness, width, wallThickness, Wall.WallType.CONCRETE);
        addWall(0, 0, wallThickness, height, Wall.WallType.CONCRETE);
        addWall(width - wallThickness, 0, wallThickness, height, Wall.WallType.CONCRETE);
        
        // Main structures - creating rooms and corridors
        addWall(150, 100, 200, 20, Wall.WallType.BRICK); // Top corridor wall
        addWall(150, 120, 20, 150, Wall.WallType.BRICK); // Left room wall
        addWall(330, 120, 20, 150, Wall.WallType.BRICK); // Right room wall
        addWall(150, 250, 200, 20, Wall.WallType.BRICK); // Bottom corridor wall
        
        // Central area structures
        addWall(400, 200, 100, 20, Wall.WallType.CONCRETE);
        addWall(520, 150, 20, 120, Wall.WallType.CONCRETE);
        addWall(600, 300, 120, 20, Wall.WallType.METAL);
        
        // Cover spots (blocks movement but not bullets)
        addWall(250, 400, 60, 40, Wall.WallType.COVER);
        addWall(450, 450, 40, 60, Wall.WallType.COVER);
        addWall(700, 200, 50, 30, Wall.WallType.COVER);
        
        // Bottom area structures
        addWall(100, 500, 180, 20, Wall.WallType.WOOD);
        addWall(350, 550, 20, 100, Wall.WallType.WOOD);
        addWall(750, 450, 20, 150, Wall.WallType.BRICK);
        
        // Spawn points
        addSpawnPoint(80, 80, SpawnPoint.Team.COUNTER_TERRORIST);
        addSpawnPoint(120, 80, SpawnPoint.Team.COUNTER_TERRORIST);
        addSpawnPoint(80, 120, SpawnPoint.Team.COUNTER_TERRORIST);
        
        addSpawnPoint(900, 650, SpawnPoint.Team.TERRORIST);
        addSpawnPoint(860, 650, SpawnPoint.Team.TERRORIST);
        addSpawnPoint(900, 610, SpawnPoint.Team.TERRORIST);
        
        // Bomb sites
        addBombSite(200, 300, 100, 80, "Site A");
        addBombSite(650, 500, 100, 80, "Site B");
    }
    
    private void createOfficeMap() {
        int wallThickness = 20;
        
        // Boundary walls
        addWall(0, 0, width, wallThickness, Wall.WallType.CONCRETE);
        addWall(0, height - wallThickness, width, wallThickness, Wall.WallType.CONCRETE);
        addWall(0, 0, wallThickness, height, Wall.WallType.CONCRETE);
        addWall(width - wallThickness, 0, wallThickness, height, Wall.WallType.CONCRETE);
        
        // Office rooms
        addWall(200, 150, 150, 20, Wall.WallType.WOOD);
        addWall(200, 170, 20, 100, Wall.WallType.WOOD);
        addWall(330, 170, 20, 100, Wall.WallType.WOOD);
        addWall(200, 250, 150, 20, Wall.WallType.WOOD);
        
        addWall(500, 200, 120, 20, Wall.WallType.WOOD);
        addWall(500, 220, 20, 80, Wall.WallType.WOOD);
        addWall(600, 220, 20, 80, Wall.WallType.WOOD);
        addWall(500, 280, 120, 20, Wall.WallType.WOOD);
        
        // Hallways
        addWall(400, 100, 20, 200, Wall.WallType.METAL);
        addWall(100, 350, 200, 20, Wall.WallType.METAL);
        
        // Office furniture (cover)
        addWall(250, 400, 40, 30, Wall.WallType.COVER);
        addWall(450, 350, 30, 40, Wall.WallType.COVER);
        addWall(650, 450, 40, 30, Wall.WallType.COVER);
        
        // Spawn points
        addSpawnPoint(60, 60, SpawnPoint.Team.COUNTER_TERRORIST);
        addSpawnPoint(100, 60, SpawnPoint.Team.COUNTER_TERRORIST);
        
        addSpawnPoint(900, 700, SpawnPoint.Team.TERRORIST);
        addSpawnPoint(860, 700, SpawnPoint.Team.TERRORIST);
        
        // Objectives
        addBombSite(250, 350, 80, 60, "Server Room");
        addBombSite(550, 450, 80, 60, "Main Office");
    }
    
    private void createMirageMap() {
        int wallThickness = 20;
        
        // Boundary walls
        addWall(0, 0, width, wallThickness, Wall.WallType.BRICK);
        addWall(0, height - wallThickness, width, wallThickness, Wall.WallType.BRICK);
        addWall(0, 0, wallThickness, height, Wall.WallType.BRICK);
        addWall(width - wallThickness, 0, wallThickness, height, Wall.WallType.BRICK);
        
        // Palace area
        addWall(150, 120, 180, 20, Wall.WallType.CONCRETE);
        addWall(150, 140, 20, 100, Wall.WallType.CONCRETE);
        addWall(310, 140, 20, 100, Wall.WallType.CONCRETE);
        
        // Connector areas
        addWall(400, 180, 100, 20, Wall.WallType.METAL);
        addWall(550, 120, 20, 180, Wall.WallType.METAL);
        
        // Apartments
        addWall(650, 200, 150, 20, Wall.WallType.BRICK);
        addWall(650, 220, 20, 120, Wall.WallType.BRICK);
        addWall(780, 220, 20, 120, Wall.WallType.BRICK);
        addWall(650, 320, 150, 20, Wall.WallType.BRICK);
        
        // Cover positions
        addWall(300, 350, 50, 40, Wall.WallType.COVER);
        addWall(500, 400, 40, 50, Wall.WallType.COVER);
        addWall(700, 450, 60, 30, Wall.WallType.COVER);
        
        // Bottom structures
        addWall(200, 500, 120, 20, Wall.WallType.WOOD);
        addWall(400, 550, 20, 100, Wall.WallType.WOOD);
        
        // Spawn points
        addSpawnPoint(70, 70, SpawnPoint.Team.COUNTER_TERRORIST);
        addSpawnPoint(110, 70, SpawnPoint.Team.COUNTER_TERRORIST);
        addSpawnPoint(70, 110, SpawnPoint.Team.COUNTER_TERRORIST);
        
        addSpawnPoint(880, 680, SpawnPoint.Team.TERRORIST);
        addSpawnPoint(920, 680, SpawnPoint.Team.TERRORIST);
        addSpawnPoint(880, 640, SpawnPoint.Team.TERRORIST);
        
        // Bomb sites
        addBombSite(220, 250, 90, 70, "Site A");
        addBombSite(700, 400, 90, 70, "Site B");
    }
    
    private void createDefaultMap() {
        createDust2Map(); // Fallback to dust2
    }
    
    private void addWall(double x, double y, double width, double height, Wall.WallType type) {
        mapElements.add(new Wall(x, y, width, height, type));
    }
    
    private void addSpawnPoint(double x, double y, SpawnPoint.Team team) {
        SpawnPoint spawn = new SpawnPoint(x, y, team);
        spawnPoints.add(spawn);
        mapElements.add(spawn);
    }
    
    private void addBombSite(double x, double y, double width, double height, String name) {
        BombSite site = new BombSite(x, y, width, height, name);
        bombSites.add(site);
        mapElements.add(site);
    }
    
    public boolean checkCollision(double x, double y) {
        for (MapElement element : mapElements) {
            if (element.blocksMovement() && element.contains(x, y)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean checkProjectileCollision(double x, double y) {
        for (MapElement element : mapElements) {
            if (element.blocksProjectiles() && element.contains(x, y)) {
                return true;
            }
        }
        return false;
    }
    
    public SpawnPoint getRandomSpawnPoint(SpawnPoint.Team team) {
        List<SpawnPoint> teamSpawns = new ArrayList<>();
        for (SpawnPoint spawn : spawnPoints) {
            if (spawn.getTeam() == team && spawn.isActive()) {
                teamSpawns.add(spawn);
            }
        }
        if (teamSpawns.isEmpty()) return null;
        
        Random random = new Random();
        return teamSpawns.get(random.nextInt(teamSpawns.size()));
    }
    
    public void render(Graphics2D g2d) {
        // Render all map elements (background is now handled by ModernUI)
        for (MapElement element : mapElements) {
            element.render(g2d);
        }
    }
    
    public List<SpawnPoint> getSpawnPoints() { return spawnPoints; }
    public List<BombSite> getBombSites() { return bombSites; }
    public String getMapName() { return mapName; }
    
    public void switchMap(String newMapName) {
        createMap(newMapName);
    }
}