# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a simple Counter Strike game prototype built with Java Swing. The project implements basic 2D game mechanics including player movement, mouse-look controls, shooting, and collision detection. It's designed as Phase 1 of a more complex game development project.

## Build Commands

- **Build the project**: `./mvnw clean compile`
- **Run tests**: `./mvnw test`
- **Run the game**: `./mvnw exec:java`
- **Package the application**: `./mvnw clean package`
- **Clean build artifacts**: `./mvnw clean`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/yen/playCS/
│   │       ├── CSGame.java              # Main game class with Swing UI
│   │       ├── GameEngine.java          # Core game logic and update loop
│   │       ├── Player.java              # Player entity with movement and shooting
│   │       ├── GameWorld.java           # Enhanced map system with multiple layouts
│   │       ├── Projectile.java          # Bullet/projectile system
│   │       ├── MapElement.java          # Base class for all map elements
│   │       ├── Wall.java                # Wall entities with different types
│   │       ├── SpawnPoint.java          # Team spawn points (T/CT)
│   │       └── BombSite.java            # Bomb site objectives
│   └── resources/
│       └── application.properties       # Configuration file
└── test/
    └── java/
        └── com/yen/playCS/
            └── PlayCsApplicationTests.java  # Unit tests for game components
```

## Architecture

- **Framework**: Java Swing for GUI and 2D graphics
- **Game Loop**: Timer-based game loop running at ~60 FPS (16ms intervals)
- **Input Handling**: KeyListener for WASD movement, MouseListener for shooting
- **Rendering**: Graphics2D with anti-aliasing for smooth visuals
- **Entity System**: Player, Projectile, and Wall entities with collision detection

## Game Controls

- **Movement**: WASD keys for player movement
- **Mouse Look**: Mouse movement controls player rotation/aim direction
- **Shooting**: Left mouse button to shoot projectiles
- **Map Switching**: 1=Dust2, 2=Office, 3=Mirage
- **UI**: Real-time display of health, ammo, position, and map info

## Enhanced Map System

- **Multiple Maps**: 3 different map layouts (Dust2, Office, Mirage)
- **Wall Types**: Concrete, Brick, Wood, Metal walls with different visual styles
- **Cover System**: Cover spots that block movement but allow bullets through
- **Team Spawn Points**: Blue (Counter-Terrorist) and Red (Terrorist) spawn areas
- **Bomb Sites**: Orange objective areas (Site A, Site B) for game modes
- **Interactive Elements**: Ready for doors, switches, and other interactive objects

## Development Notes

- Compatible with Java 8 (builds successfully with current environment)  
- Uses standard Java Swing - no external game frameworks required
- Enhanced collision detection system (separate for movement vs projectiles)
- Object-oriented map architecture with inheritance (MapElement base class)
- Easy to add new map elements, wall types, and interactive objects
- Ready for Phase 2 expansion (AI enemies, multiplayer, game modes)