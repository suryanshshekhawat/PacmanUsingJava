package pacman.model.entity.dynamic.ghost;

import pacman.model.entity.dynamic.DynamicEntity;
import pacman.model.entity.dynamic.player.observer.PlayerPositionObserver;

import java.util.Map;

/**
 * Represents Ghost entity in Pac-Man Game
 */
public interface Ghost extends DynamicEntity, PlayerPositionObserver {

    /***
     * Sets the speeds of the Ghost for each GhostMode
     * @param speeds speeds of the Ghost for each GhostMode
     */
    void setSpeeds(Map<GhostMode, Double> speeds);

    /**
     * Sets the mode of the Ghost used to calculate target position
     *
     * @param ghostMode mode of the Ghost
     */
    void setGhostMode(GhostMode ghostMode);

    /**
     * Gets the type of Ghost : BLINKY, INKY, CLYDE, PINKY
     *
     * @return Type of Ghost as a char
     */
    char getRenderableType();

    void setFrightenedMode(boolean isFrightened);

    boolean isFrightenedMode();

    void resetToStartingPosition();
}
