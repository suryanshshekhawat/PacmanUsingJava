package pacman.model.entity.dynamic.ghost.Strategy;

import pacman.model.entity.dynamic.ghost.Ghost;
import pacman.model.entity.dynamic.ghost.Strategy.MovementStrategy;
import pacman.model.entity.dynamic.physics.Direction;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.player.Pacman;

/**
 * The PINKYStrategy class implements the MovementStrategy for the ghost named Pinky.
 * Pinky tries to ambush Pac-Man by targeting a position four tiles ahead of Pac-Man's
 * current direction.
 */
public class PINKYStrategy implements MovementStrategy {

    /**
     * Calculates Pinky's target position. Pinky aims for a point four tiles ahead of
     * Pac-Man's current direction, creating an ambush strategy.
     *
     * @param pacman      the Pac-Man entity whose position and direction are used for targeting
     * @param currentGhost the ghost (Pinky) using this movement strategy
     * @return the target position, which is four tiles ahead of Pac-Man's current direction
     */
    @Override
    public Vector2D calculateTarget(Pacman pacman, Ghost currentGhost) {
        Vector2D directionVector = directionToVector(pacman.getDirection());
        Vector2D target = pacman.getPosition().add(directionVector.scale(4));
        return target;
    }

    /**
     * Converts a Direction enum to a corresponding Vector2D.
     *
     * @param direction the direction Pac-Man is facing
     * @return a Vector2D representing the direction as a vector
     */
    private Vector2D directionToVector(Direction direction) {
        switch (direction) {
            case UP:
                return new Vector2D(0, -1);
            case DOWN:
                return new Vector2D(0, 1);
            case LEFT:
                return new Vector2D(-1, 0);
            case RIGHT:
                return new Vector2D(1, 0);
            default:
                return new Vector2D(0, 0);
        }
    }
}
