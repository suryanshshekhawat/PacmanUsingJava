package pacman.model.entity.dynamic.ghost.State;

import pacman.model.entity.dynamic.ghost.GhostImpl;

import java.util.Random;

/**
 * The FrightenedState class represents the frightened behavior of a ghost in the Pac-Man game.
 * In this state, the ghosts run around frightened by the player with added Logic in GhostImpl.
 */
public class FrightenedState implements GhostState {
    private final GhostImpl ghost;

    /**
     * Constructs a new FrightenedState for the specified ghost.
     *
     * @param ghost the ghost that will be in the frightened state
     */
    public FrightenedState(GhostImpl ghost) {
        this.ghost = ghost;
    }

    /**
     * Enters the frightened state and sets the ghost's speed to its frightened speed.
     * This method is called when the ghost transitions into the frightened state.
     */
    @Override
    public void enterState() {
        ghost.kinematicState.setSpeed(ghost.getFrightenedSpeed());
        System.out.println("ENTERED FRIGHTENED STATE");
    }

    /**
     * Exits the frightened state. This method is called when the ghost transitions
     * out of the frightened state. No specific behavior is defined in this implementation.
     */
    @Override
    public void exitState() {
    }

    /**
     * Updates the ghost's target location. This method is called periodically to
     * adjust the ghost's movement. No specific target update logic is defined here.
     * Random movement behavior is handled in the GhostImpl class.
     */
    @Override
    public void updateTargetLocation() {
    }
}
