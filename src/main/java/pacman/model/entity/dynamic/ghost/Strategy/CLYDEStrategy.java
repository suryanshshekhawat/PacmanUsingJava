package pacman.model.entity.dynamic.ghost.Strategy;

import pacman.model.entity.dynamic.ghost.Ghost;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.player.Pacman;

/**
 * The CLYDEStrategy class implements the MovementStrategy for the ghost named Clyde.
 * Clyde has a unique behavior: he targets Pac-Man's position when far from him,
 * but switches to targeting the bottom-left corner when he gets too close.
 */
public class CLYDEStrategy implements MovementStrategy {

    /**
     * Calculates Clyde's target position based on his distance from Pac-Man.
     * If Clyde is more than 8 units away from Pac-Man, he targets Pac-Man's position.
     * Otherwise, he targets the bottom-left corner of the game map.
     *
     * @param pacman      the Pac-Man entity whose position is considered
     * @param currentGhost the ghost (Clyde) using this movement strategy
     * @return the target position for Clyde, either Pac-Man's position or the bottom-left corner
     */
    @Override
    public Vector2D calculateTarget(Pacman pacman, Ghost currentGhost) {
        if (currentGhost.getPosition().distance(pacman.getPosition()) > 8) {
            return pacman.getPosition();
        } else {
            return new Vector2D(0, 576);
        }
    }
}
