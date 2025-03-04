package pacman.model.entity.dynamic.ghost.Strategy;

import pacman.model.entity.dynamic.ghost.Ghost;
import pacman.model.entity.dynamic.physics.Direction;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.player.Pacman;

/**
 * The INKYStrategy class implements the MovementStrategy for the ghost named Inky.
 * Inky's targeting logic is more complex, involving both Pac-Man's position and Blinky's position.
 * The target is calculated based on a point two tiles ahead of Pac-Man, and a vector is extended
 * from Blinky's position to that point, which is then doubled.
 */
public class INKYStrategy implements MovementStrategy {

    /**
     * Calculates Inky's target position. This method is left unimplemented, as the main
     * logic is in the overloaded method that considers Blinky's position.
     *
     * @param pacman      the Pac-Man entity
     * @param currentGhost the ghost (Inky) using this strategy
     * @return null, since this method does not implement the targeting logic
     */
    @Override
    public Vector2D calculateTarget(Pacman pacman, Ghost currentGhost) {
        return null;
    }

    /**
     * Calculates Inky's target position based on Pac-Man's position, direction, and Blinky's position.
     * The target is determined by finding a point two tiles ahead of Pac-Man, calculating a vector
     * from Blinky to that point, and then doubling the vector.
     *
     * @param pacman      the Pac-Man entity
     * @param currentGhost the ghost (Inky) using this strategy
     * @param blinky      the ghost (Blinky) whose position is used in the targeting calculation
     * @return the calculated target position for Inky
     */
    public Vector2D calculateTarget(Pacman pacman, Ghost currentGhost, Ghost blinky) {
        Vector2D directionVector = directionToVector(pacman.getDirection());
        Vector2D target = pacman.getPosition().add(directionVector.scale(2));

        Vector2D blinkyToTarget = target.subtract(blinky.getPosition());
        return blinky.getPosition().add(blinkyToTarget.scale(2));
    }

    /**
     * Converts a Direction enum to a corresponding Vector2D.
     *
     * @param direction the direction Pac-Man is facing
     * @return a Vector2D representing the direction as a vector
     */
    private Vector2D directionToVector(Direction direction) {
        return switch (direction) {
            case UP -> new Vector2D(0, -1);
            case DOWN -> new Vector2D(0, 1);
            case LEFT -> new Vector2D(-1, 0);
            case RIGHT -> new Vector2D(1, 0);
            default -> new Vector2D(0, 0);
        };
    }
}