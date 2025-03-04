package pacman.model.factories.Prototype;

import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.Vector2D;

/**
 * The PelletPrototype interface extends the Renderable interface and provides
 * a method for creating a clone of the pellet with a new position.
 * This interface is part of the Prototype design pattern, allowing the creation
 * of duplicate pellets with different positions.
 */
public interface PelletPrototype extends Renderable {

    /**
     * Creates a clone of the pellet with the specified position.
     *
     * @param position the new position for the cloned pellet
     * @return a new PelletPrototype object with the specified position
     */
    PelletPrototype createCloneWithPosition(Vector2D position);
}