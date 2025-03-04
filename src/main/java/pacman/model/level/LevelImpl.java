package pacman.model.level;

import org.json.simple.JSONObject;
import pacman.ConfigurationParseException;
import pacman.model.engine.observer.GameState;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.DynamicEntity;
import pacman.model.entity.dynamic.ghost.Ghost;
import pacman.model.entity.dynamic.ghost.GhostImpl;
import pacman.model.entity.dynamic.ghost.GhostMode;
import pacman.model.entity.dynamic.physics.PhysicsEngine;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.player.Controllable;
import pacman.model.entity.dynamic.player.Pacman;
import pacman.model.entity.staticentity.StaticEntity;
import pacman.model.entity.staticentity.collectable.Collectable;
import pacman.model.entity.staticentity.collectable.Pellet;
import pacman.model.factories.Prototype.PelletRegistry;
import pacman.model.factories.Prototype.PowerPellet;
import pacman.model.factories.RenderableType;
import pacman.model.level.observer.LevelStateObserver;
import pacman.model.maze.Maze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Concrete implement of Pac-Man level
 */
public class LevelImpl implements Level {

    private static final int START_LEVEL_TIME = 100;
    private final Maze maze;
    private final List<LevelStateObserver> observers;
    private List<Renderable> renderables;
    private Controllable player;
    private List<Ghost> ghosts;
    private int tickCount;
    private Map<GhostMode, Integer> modeLengths;
    private int numLives;
    private int points;
    private GameState gameState;
    private List<Renderable> collectables;
    private GhostMode currentGhostMode;
    private PelletRegistry pelletRegistry = new PelletRegistry();
    private int frightenedDuration;
    private int frightenedModeTimer;

    public LevelImpl(JSONObject levelConfiguration,
                     Maze maze) {
        this.renderables = new ArrayList<>();
        this.maze = maze;
        this.tickCount = 0;
        this.observers = new ArrayList<>();
        this.modeLengths = new HashMap<>();
        this.gameState = GameState.READY;
        this.currentGhostMode = GhostMode.SCATTER;
        this.points = 0;

        initLevel(new LevelConfigurationReader(levelConfiguration));
    }

    private void initLevel(LevelConfigurationReader levelConfigurationReader) {
        // Fetch all renderables for the level
        this.renderables = maze.getRenderables();

        // Set up player
        if (!(maze.getControllable() instanceof Controllable)) {
            throw new ConfigurationParseException("Player entity is not controllable");
        }
        this.player = (Controllable) maze.getControllable();
        this.player.setSpeed(levelConfigurationReader.getPlayerSpeed());
        setNumLives(maze.getNumLives());

        // Set up ghosts
        this.ghosts = maze.getGhosts().stream()
                .map(element -> (Ghost) element)
                .collect(Collectors.toList());
        Map<GhostMode, Double> ghostSpeeds = levelConfigurationReader.getGhostSpeeds();

        for (Ghost ghost : this.ghosts) {
            player.registerObserver(ghost);
            ghost.setSpeeds(ghostSpeeds);
            ghost.setGhostMode(this.currentGhostMode);
        }
        this.modeLengths = levelConfigurationReader.getGhostModeLengths();

        // Set up collectables using the PelletRegistry clones
        this.collectables = maze.getPellets().stream()
                .map(pellet -> {
                    Vector2D position = ((Pellet) pellet).getPosition(); // Assuming pellets have bounding boxes
                    if (((Pellet) pellet).isPowerPellet()) {
                        return (Collectable) pelletRegistry.getPellet("powerPellet", position);
                    } else {
                        return (Collectable) pelletRegistry.getPellet("pellet", position);
                    }
                })
                .collect(Collectors.toList());

        Integer loadedFrightenedDuration = levelConfigurationReader.getGhostModeLengths().get(GhostMode.FRIGHTENED);
        this.frightenedDuration = (loadedFrightenedDuration != null) ? loadedFrightenedDuration : 5; // in seconds
    }

    @Override
    public List<Renderable> getRenderables() {
        return this.renderables;
    }

    private List<DynamicEntity> getDynamicEntities() {
        return renderables.stream().filter(e -> e instanceof DynamicEntity).map(e -> (DynamicEntity) e).collect(
                Collectors.toList());
    }

    private List<StaticEntity> getStaticEntities() {
        return renderables.stream().filter(e -> e instanceof StaticEntity).map(e -> (StaticEntity) e).collect(
                Collectors.toList());
    }

    @Override
    public void tick() {
        if (frightenedModeTimer > 0) {
            frightenedModeTimer--;
            if (frightenedModeTimer == 0) {
                for (Ghost ghost : ghosts) {
                    ghost.setFrightenedMode(false);
                }
            }
        }

        if (this.gameState != GameState.IN_PROGRESS) {
            if (tickCount >= START_LEVEL_TIME) {
                setGameState(GameState.IN_PROGRESS);
                tickCount = 0;
            }
        } else {
            // Update ghost modes based on timing
            if (tickCount == modeLengths.get(currentGhostMode)) {
                this.currentGhostMode = GhostMode.getNextGhostMode(currentGhostMode);
                for (Ghost ghost : this.ghosts) {
                    ghost.setGhostMode(this.currentGhostMode);
                }
                tickCount = 0;
            }

            if (tickCount % Pacman.PACMAN_IMAGE_SWAP_TICK_COUNT == 0) {
                this.player.switchImage();
            }

            // Update dynamic entities
            List<DynamicEntity> dynamicEntities = getDynamicEntities();
            for (DynamicEntity dynamicEntity : dynamicEntities) {
                maze.updatePossibleDirections(dynamicEntity);
                dynamicEntity.update();
            }

            for (int i = 0; i < dynamicEntities.size(); ++i) {
                DynamicEntity dynamicEntityA = dynamicEntities.get(i);

                // Handle collisions with other dynamic entities (e.g., Pacman and ghosts)
                for (int j = i + 1; j < dynamicEntities.size(); ++j) {
                    DynamicEntity dynamicEntityB = dynamicEntities.get(j);

                    if (dynamicEntityA.collidesWith(dynamicEntityB) || dynamicEntityB.collidesWith(dynamicEntityA)) {
                        // Handle collision based on whether a ghost is in frightened mode
                        if (isPlayer(dynamicEntityA) && dynamicEntityB instanceof Ghost ghost) {
                            handlePacmanGhostCollision(ghost);
                        } else if (isPlayer(dynamicEntityB) && dynamicEntityA instanceof Ghost ghost) {
                            handlePacmanGhostCollision(ghost);
                        } else {
                            dynamicEntityA.collideWith(this, dynamicEntityB);
                            dynamicEntityB.collideWith(this, dynamicEntityA);
                        }
                    }
                }

                // Handle collisions with static entities
                for (StaticEntity staticEntity : getStaticEntities()) {
                    if (dynamicEntityA.collidesWith(staticEntity)) {
                        dynamicEntityA.collideWith(this, staticEntity);
                        PhysicsEngine.resolveCollision(dynamicEntityA, staticEntity);
                    }
                }
            }
        }

        tickCount++;
    }

    private void handlePacmanGhostCollision(Ghost ghost) {
        if (ghost.isFrightenedMode()) {
            points += 100; // Add points to Pacman
            notifyObserversWithScoreChange(100);
            ghost.resetToStartingPosition(); // Reset the ghost
        } else {
            handleLoseLife();
        }
    }




    @Override
    public boolean isPlayer(Renderable renderable) {
        return renderable == this.player;
    }

    @Override
    public boolean isCollectable(Renderable renderable) {
        return maze.getPellets().contains(renderable) && ((Collectable) renderable).isCollectable();
    }

    @Override
    public void collect(Collectable collectable) {
        this.points += collectable.getPoints();
        notifyObserversWithScoreChange(collectable.getPoints());
        this.collectables.remove(collectable);

        // Check if the collectable is a power pellet
        if (collectable instanceof PowerPellet) {
            activateFrightenedMode();
        }
    }

    private void activateFrightenedMode() {
        frightenedModeTimer = frightenedDuration * 30; // our tickRate !

        // Set all ghosts to frightened mode
        for (Ghost ghost : this.ghosts) {
            ghost.setFrightenedMode(true);
        }
    }

    @Override
    public void handleLoseLife() {
        if (gameState == GameState.IN_PROGRESS) {
            for (DynamicEntity dynamicEntity : getDynamicEntities()) {
                dynamicEntity.reset();
            }
            setNumLives(numLives - 1);
            setGameState(GameState.READY);
            tickCount = 0;
        }
    }

    @Override
    public void moveLeft() {
        player.left();
    }

    @Override
    public void moveRight() {
        player.right();
    }

    @Override
    public void moveUp() {
        player.up();
    }

    @Override
    public void moveDown() {
        player.down();
    }

    @Override
    public boolean isLevelFinished() {
        return collectables.isEmpty();
    }

    @Override
    public void registerObserver(LevelStateObserver observer) {
        this.observers.add(observer);
        observer.updateNumLives(this.numLives);
        observer.updateGameState(this.gameState);
    }

    @Override
    public void removeObserver(LevelStateObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObserversWithNumLives() {
        for (LevelStateObserver observer : observers) {
            observer.updateNumLives(this.numLives);
        }
    }

    private void setGameState(GameState gameState) {
        this.gameState = gameState;
        notifyObserversWithGameState();
    }

    @Override
    public void notifyObserversWithGameState() {
        for (LevelStateObserver observer : observers) {
            observer.updateGameState(gameState);
        }
    }

    /**
     * Notifies observer of change in player's score
     */
    public void notifyObserversWithScoreChange(int scoreChange) {
        for (LevelStateObserver observer : observers) {
            observer.updateScore(scoreChange);
        }
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public int getNumLives() {
        return this.numLives;
    }

    private void setNumLives(int numLives) {
        this.numLives = numLives;
        notifyObserversWithNumLives();
    }

    @Override
    public void handleGameEnd() {
        this.renderables.removeAll(getDynamicEntities());
    }

    public Ghost getBlinky() {
        for (Ghost ghost : this.ghosts) {
            if (ghost.getRenderableType() == 'b') {
                return ghost;
            }
        }
        return null;
    }
}
