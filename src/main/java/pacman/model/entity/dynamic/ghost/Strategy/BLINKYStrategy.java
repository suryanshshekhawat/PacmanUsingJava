package pacman.model.entity.dynamic.ghost.Strategy;

import pacman.model.entity.dynamic.ghost.Ghost;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.player.Pacman;

/**
 * The BLINKYStrategy class implements the MovementStrategy for the ghost named Blinky.
 * Blinky targets Pac-Man's current position, making him a direct chaser.
 */
public class BLINKYStrategy implements MovementStrategy {

    /**
     * Calculates Blinky's target position, which is Pac-Man's current position.
     *
     * @param pacman      the Pac-Man entity whose position is targeted
     * @param currentGhost the ghost (Blinky) using this movement strategy
     * @return the target position, which is Pac-Man's current position
     */
    @Override
    public Vector2D calculateTarget(Pacman pacman, Ghost currentGhost) {
        return pacman.getPosition();
    }
}
