# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.4 application named `playCS` - a demo/playground project for learning and experimenting with Spring Boot and computer science concepts. The project uses Java 17 and Maven as the build tool.

## Build Commands

**Important**: This project requires Java 17, but the current environment has Java 8 which causes build failures. The build commands will fail until Java 17 is installed.

- **Build the project**: `./mvnw clean compile`
- **Run tests**: `./mvnw test`
- **Package the application**: `./mvnw clean package`
- **Run the application**: `./mvnw spring-boot:run`
- **Clean build artifacts**: `./mvnw clean`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/yen/playCS/
│   │       └── PlayCsApplication.java          # Main Spring Boot application class
│   └── resources/
│       └── application.properties              # Spring Boot configuration
└── test/
    └── java/
        └── com/yen/playCS/
            └── PlayCsApplicationTests.java     # Basic Spring Boot test
```

## Architecture

- **Framework**: Spring Boot 3.5.4 with minimal starter dependencies
- **Package Structure**: Single package `com.yen.playCS` 
- **Main Class**: `PlayCsApplication.java:7` - Standard Spring Boot main class with `@SpringBootApplication`
- **Configuration**: Basic Spring Boot setup with `application.properties:1` setting the application name
- **Testing**: Uses JUnit 5 via `spring-boot-starter-test`

## Development Notes

- The project is currently a minimal Spring Boot skeleton with just the basic starter dependency
- Uses Maven Wrapper (`./mvnw`) for consistent build experience
- The project follows standard Maven directory layout
- Currently has only a context loading test in `PlayCsApplicationTests.java:10`