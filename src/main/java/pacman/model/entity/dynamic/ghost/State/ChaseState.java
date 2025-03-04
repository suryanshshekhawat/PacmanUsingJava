package pacman.model.entity.dynamic.ghost.State;

import pacman.model.entity.dynamic.ghost.GhostImpl;

/**
 * The ChaseState class represents the chase behavior of a ghost in the Pac-Man game.
 * In this state, the ghost pursues the player by updating its target location to the player's position.
 */
public class ChaseState implements GhostState {
    private final GhostImpl ghost;

    /**
     * Constructs a new ChaseState for the specified ghost.
     *
     * @param ghost the ghost that will be in the chase state
     */
    public ChaseState(GhostImpl ghost) {
        this.ghost = ghost;
    }

    /**
     * Enters the chase state and sets the ghost's speed to its chase speed.
     * This method is called when the ghost transitions into the chase state.
     */
    @Override
    public void enterState() {
        ghost.kinematicState.setSpeed(ghost.getChaseSpeed());
        System.out.println("ENTERED CHASE STATE");
    }

    /**
     * Exits the chase state. This method is called when the ghost transitions
     * out of the chase state. No specific behavior is defined in this implementation.
     */
    @Override
    public void exitState() {
    }

    /**
     * Updates the ghost's target location to the player's current position.
     * This method is called periodically to adjust the ghost's target while in the chase state.
     */
    @Override
    public void updateTargetLocation() {
        ghost.setTargetLocation(ghost.getPlayerPosition());
    }
}
