# Project README: Design Pattern Guide

## Overview
This project implements three key design patterns: **Prototype**, **State**, and **Strategy**. Each pattern plays a vital role in managing game behaviors, object creation, and encapsulating algorithms.

Below are detailed instructions on where to locate each design pattern within the codebase.

---

## 1. Prototype Pattern
- **Location**: `src/pacman/model/factories/Prototype/`

- **Key Components**:
    - **Interface**: `PelletPrototype.java`
    - **Concrete Prototype**: `PowerPellet.java`
    - **Registry**: `PelletRegistry.java`

- **Description**: The **Prototype Pattern** is used for creating instances of pellets. `PelletRegistry` manages different types of pellet prototypes, enabling the cloning of objects to generate new pellets with specific attributes.

---

## 2. State Pattern
- **Location**: `src/pacman/model/entity/dynamic/ghost/State/`

- **Key Components**:
    - **Context**: `GhostImpl.java` (found in `src/pacman/model/entity/dynamic/ghost/`)
    - **State Interface**: `GhostState.java`
    - **Concrete States**: `ChaseState.java`, `ScatterState.java`, `FrightenedState.java`

- **Description**: The **State Pattern** manages the different behaviors of ghosts. `GhostImpl` acts as the context, switching between states such as `ChaseState`, `ScatterState`, and `FrightenedState`, each defining unique ghost behaviors.

---

## 3. Strategy Pattern
- **Location**: `src/pacman/model/entity/dynamic/ghost/Strategy/`

- **Key Components**:
    - **Strategy Interface**: `MovementStrategy.java`
    - **Concrete Strategies**: `BLINKYStrategy.java`, `CLYDEStrategy.java`, `INKYStrategy.java`, `PINKYStrategy.java`

- **Description**: The **Strategy Pattern** encapsulates the ghost movement algorithms. Different strategies (e.g., `BLINKYStrategy`, `CLYDEStrategy`) define how each ghost targets Pac-Man, allowing for flexible and interchangeable movement behaviors.

---

## How to Navigate the Code
- **src/**: The root directory for source code.

- Follow the relative paths provided above to find the implementation of each design pattern. Each pattern is organized in separate subdirectories, ensuring a clear and modular structure.

---
