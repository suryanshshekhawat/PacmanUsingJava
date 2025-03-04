package pacman.model.entity.dynamic.ghost.State;

import pacman.model.entity.dynamic.ghost.GhostImpl;

/**
 * The ScatterState class represents the scatter behavior of a ghost in the Pac-Man game.
 * In this state, the ghost moves towards its assigned corner on the game map to scatter away from the player.
 */
public class ScatterState implements GhostState {
    private final GhostImpl ghost;

    /**
     * Constructs a new ScatterState for the specified ghost.
     *
     * @param ghost the ghost that will be in the scatter state
     */
    public ScatterState(GhostImpl ghost) {
        this.ghost = ghost;
    }

    /**
     * Enters the scatter state and sets the ghost's speed to its scatter speed.
     * This method is called when the ghost transitions into the scatter state.
     */
    @Override
    public void enterState() {
        ghost.kinematicState.setSpeed(ghost.getScatterSpeed()); // Set speed for scatter mode
        System.out.println("ENTERED SCATTER STATE");
    }

    /**
     * Exits the scatter state. This method is called when the ghost transitions
     * out of the scatter state. No specific cleanup is necessary in this implementation.
     */
    @Override
    public void exitState() {
        // Optional cleanup if necessary
    }

    /**
     * Updates the ghost's target location to its assigned corner on the map.
     * This method is called periodically to adjust the ghost's target while in the scatter state.
     */
    @Override
    public void updateTargetLocation() {
        ghost.setTargetLocation(ghost.getTargetCorner());
    }
}
