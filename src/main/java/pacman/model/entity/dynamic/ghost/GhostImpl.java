package pacman.model.entity.dynamic.ghost;

import javafx.scene.image.Image;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.ghost.State.ChaseState;
import pacman.model.entity.dynamic.ghost.State.GhostState;
import pacman.model.entity.dynamic.ghost.State.ScatterState;
import pacman.model.entity.dynamic.ghost.Strategy.MovementStrategy;
import pacman.model.entity.dynamic.physics.*;
import pacman.model.factories.RenderableType;
import pacman.model.level.Level;
import pacman.model.maze.Maze;
import pacman.model.level.LevelConfigurationReader;

import java.util.*;

/**
 * Concrete implementation of Ghost entity in Pac-Man Game
 */
public class GhostImpl implements Ghost {

    private static final int minimumDirectionCount = 8;
    private final Layer layer = Layer.FOREGROUND;
    private Image image;
    private final BoundingBox boundingBox;
    private final Vector2D startingPosition;
    private final Vector2D targetCorner;
    public KinematicState kinematicState;
    private GhostMode ghostMode;
    private Vector2D targetLocation;
    private Vector2D playerPosition;
    private Direction currentDirection;
    private Set<Direction> possibleDirections;
    private Map<GhostMode, Double> speeds;
    private int currentDirectionCount = 0;
    public char GhostType;

    // for State pattern implementation
    private final int chaseDuration;
    private final int scatterDuration;
    private final int frightenedDuration;

    private final double chaseSpeed;
    private final double scatterSpeed;
    private final double frightenedSpeed;

    private GhostState currentState;
    private long stateStartTime;

    private MovementStrategy strategy;

    private Image originalImage;
    private static final Image frightenedImage = new Image("maze/ghosts/frightened.png"); // Frightened image path

    public GhostImpl(Image image, BoundingBox boundingBox, KinematicState kinematicState, GhostMode ghostMode, Vector2D targetCorner, LevelConfigurationReader configReader, MovementStrategy strategy, char GhostType) {
        this.image = image;
        this.originalImage = this.image;
        this.boundingBox = boundingBox;
        this.kinematicState = kinematicState;
        this.startingPosition = kinematicState.getPosition();
        this.ghostMode = ghostMode;
        this.possibleDirections = new HashSet<>();
        this.targetCorner = targetCorner;
        this.targetLocation = getTargetLocation();
        this.currentDirection = null;
        this.currentState = new ScatterState(this);
        this.strategy = strategy;
        this.GhostType = GhostType;

        // for state pattern implementation
        Map<GhostMode, Integer> modeLengths = configReader.getGhostModeLengths();
        this.chaseDuration = modeLengths.get(GhostMode.CHASE);
        this.scatterDuration = modeLengths.get(GhostMode.SCATTER);
        this.frightenedDuration = modeLengths.get(GhostMode.FRIGHTENED);

        Map<GhostMode, Double> ghostSpeeds = configReader.getGhostSpeeds();
        this.chaseSpeed = ghostSpeeds.get(GhostMode.CHASE);
        this.scatterSpeed = ghostSpeeds.get(GhostMode.SCATTER);
        this.frightenedSpeed = ghostSpeeds.get(GhostMode.FRIGHTENED);

        // start in scatter mode initially
        this.currentState = new ScatterState(this);
        this.stateStartTime = System.currentTimeMillis();
    }

    @Override
    public void setSpeeds(Map<GhostMode, Double> speeds) {
        this.speeds = speeds;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public void update() {
        currentState.updateTargetLocation();
        this.updateDirection();
        this.kinematicState.update();
        this.boundingBox.setTopLeft(this.kinematicState.getPosition());

        // Calculate the time elapsed in the current state
        long timeInCurrentState = System.currentTimeMillis() - stateStartTime;
        int currentDurationMillis = getCurrentModeDuration() * 1000;

        // Check if the time threshold has been met for switching state
        if (timeInCurrentState >= currentDurationMillis) {
            System.out.println("Time threshold met for " + ghostMode + " mode, switching state.");
            switchState();
        }

        // Log the current mode and time periodically for debugging
        if (timeInCurrentState % 1000 < 50) {
            System.out.println("Current Mode: " + ghostMode + ", Time in Current State (ms): " + timeInCurrentState +
                    ", Duration (ms): " + currentDurationMillis);
        }
    }

    private int getCurrentModeDuration() {
        return switch (this.ghostMode) {
            case CHASE -> chaseDuration;
            case SCATTER -> scatterDuration;
            case FRIGHTENED -> frightenedDuration;
        };
    }

    private void updateDirection() {
        // Ghosts update their target location when they reach an intersection
        if (Maze.isAtIntersection(this.possibleDirections)) {
            this.targetLocation = getTargetLocation();
        }

        Direction newDirection = selectDirection(possibleDirections);

        // Ghosts have to continue in a direction for a minimum time before changing direction
        if (this.currentDirection != newDirection) {
            this.currentDirectionCount = 0;
        }
        this.currentDirection = newDirection;

        switch (currentDirection) {
            case LEFT -> this.kinematicState.left();
            case RIGHT -> this.kinematicState.right();
            case UP -> this.kinematicState.up();
            case DOWN -> this.kinematicState.down();
        }
    }

    private Vector2D getTargetLocation() {
        return switch (this.ghostMode) {
            case CHASE -> this.playerPosition;
            case SCATTER -> this.targetCorner;
            case FRIGHTENED -> (this.targetLocation != null) ? this.targetLocation : this.kinematicState.getPosition(); // fallback to current position
        };
    }


    private Direction selectDirection(Set<Direction> possibleDirections) {
        if (possibleDirections.isEmpty()) {
            return currentDirection;
        }

        // Prevent direction changes if targetLocation is null
        if (targetLocation == null) {
            return currentDirection;
        }

        Map<Direction, Double> distances = new HashMap<>();
        for (Direction direction : possibleDirections) {
            // ghosts never choose to reverse travel
            if (currentDirection == null || direction != currentDirection.opposite()) {
                Vector2D potentialPosition = this.kinematicState.getPotentialPosition(direction);
                if (potentialPosition != null) {
                    distances.put(direction, Vector2D.calculateEuclideanDistance(potentialPosition, this.targetLocation));
                }
            }
        }

        // Only go the opposite way if trapped
        if (distances.isEmpty()) {
            return currentDirection.opposite();
        }

        // Select the direction that will reach the target location fastest
        return Collections.min(distances.entrySet(), Map.Entry.comparingByValue()).getKey();
    }


    @Override
    public void setGhostMode(GhostMode ghostMode) {
        this.ghostMode = ghostMode;
        this.kinematicState.setSpeed(speeds.get(ghostMode));

        // ensure direction is switched
        this.currentDirectionCount = minimumDirectionCount;
    }

    @Override
    public boolean collidesWith(Renderable renderable) {
        return boundingBox.collidesWith(kinematicState.getSpeed(), kinematicState.getDirection(), renderable.getBoundingBox());
    }

    @Override
    public void collideWith(Level level, Renderable renderable) {
        if (level.isPlayer(renderable)) {
            level.handleLoseLife(); // This might override your custom logic
        }
    }


    @Override
    public void update(Vector2D playerPosition) {
        this.playerPosition = playerPosition;
    }

    @Override
    public Vector2D getPositionBeforeLastUpdate() {
        return this.kinematicState.getPreviousPosition();
    }

    @Override
    public double getHeight() {
        return this.boundingBox.getHeight();
    }

    @Override
    public double getWidth() {
        return this.boundingBox.getWidth();
    }

    @Override
    public Vector2D getPosition() {
        return this.kinematicState.getPosition();
    }

    @Override
    public void setPosition(Vector2D position) {
        this.kinematicState.setPosition(position);
    }

    @Override
    public Layer getLayer() {
        return this.layer;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public void reset() {
        // return ghost to starting position
        this.kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                .setPosition(startingPosition)
                .build();
        this.boundingBox.setTopLeft(startingPosition);
        this.ghostMode = GhostMode.SCATTER;
        this.currentDirectionCount = minimumDirectionCount;
    }

    @Override
    public void setPossibleDirections(Set<Direction> possibleDirections) {
        this.possibleDirections = possibleDirections;
    }

    @Override
    public Direction getDirection() {
        return this.kinematicState.getDirection();
    }

    @Override
    public Vector2D getCenter() {
        return new Vector2D(boundingBox.getMiddleX(), boundingBox.getMiddleY());
    }

    @Override
    public char getRenderableType() {
        return this.GhostType;
    }

    public void switchState() {
        System.out.println("Switching from " + ghostMode + " to the next state.");

        // Exit the current state
        currentState.exitState();

        // Determine the next mode and set it
        ghostMode = GhostMode.getNextGhostMode(ghostMode);
        currentState = ghostMode == GhostMode.CHASE ? new ChaseState(this) : new ScatterState(this);

        // Enter the new state and reset the start time
        currentState.enterState();
        stateStartTime = System.currentTimeMillis(); // Start timing for the new state
        System.out.println("ENTERED " + ghostMode + " STATE at time: " + stateStartTime);
    }

    public double getScatterSpeed() {
        return this.scatterSpeed;
    }

    public double getChaseSpeed() {
        return this.chaseSpeed;
    }

    public double getFrightenedSpeed() {
        return this.frightenedSpeed;
    }

    public Vector2D getPlayerPosition() {
        return this.playerPosition;
    }

    public void setTargetLocation(Vector2D targetLocation) {
        this.targetLocation = targetLocation;
    }

    public Vector2D getTargetCorner() {
        return this.targetCorner;
    }

    @Override
    public void setFrightenedMode(boolean isFrightened) {
        if (isFrightened) {
            this.image = this.frightenedImage; // Set to frightened image
            this.setGhostMode(GhostMode.FRIGHTENED);
        } else {
            this.image = this.originalImage; // Revert to original image
            this.setGhostMode(GhostMode.SCATTER); // Or revert to another mode as needed
        }
    }

    public void resetToStartingPosition() {
        this.kinematicState.setPosition(startingPosition);
        this.boundingBox.setTopLeft(startingPosition);
        this.setFrightenedMode(false); // Optionally, turn off frightened mode when reset
    }

    public boolean isFrightenedMode() {
        return this.ghostMode == GhostMode.FRIGHTENED;
    }

}
