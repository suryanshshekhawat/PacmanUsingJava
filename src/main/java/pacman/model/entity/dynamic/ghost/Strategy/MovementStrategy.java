package pacman.model.entity.dynamic.ghost.Strategy;

import pacman.model.entity.dynamic.ghost.Ghost;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.dynamic.player.Pacman;

/**
 * The MovementStrategy interface defines a strategy for calculating the target position
 * for a ghost in the Pac-Man game. Implementing classes will provide specific behavior
 * for different ghosts, determining how they target Pac-Man or specific locations on the map.
 */
public interface MovementStrategy {

    /**
     * Calculates the target position for the ghost based on the current game state.
     *
     * @param pacman      the Pac-Man entity, whose position and state are used for targeting
     * @param currentGhost the ghost using this movement strategy
     * @return a Vector2D representing the target position for the ghost
     */
    Vector2D calculateTarget(Pacman pacman, Ghost currentGhost);
}