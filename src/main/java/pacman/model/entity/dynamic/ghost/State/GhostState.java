package pacman.model.entity.dynamic.ghost.State;

public interface GhostState {
    // Called when entering a new state
    void enterState();

    // Called when exiting the current state
    void exitState();

    void updateTargetLocation();
}