package pacman.model.factories.Prototype;

import javafx.scene.image.Image;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.staticentity.collectable.Pellet;

import java.util.HashMap;
import java.util.Map;

/**
 * The PelletRegistry class serves as a registry for managing and cloning different types of pellets
 * using the Prototype design pattern. It holds a collection of pellet prototypes that can be
 * cloned with a specified position.
 */
public class PelletRegistry {

    private Map<String, PelletPrototype> prototypes = new HashMap<>();

    /**
     * Constructs a PelletRegistry and initializes it with default pellet prototypes.
     * This includes a regular pellet and a power pellet with their respective images and attributes.
     */
    public PelletRegistry() {
        prototypes.put("pellet", (PelletPrototype) new Pellet(null, Renderable.Layer.BACKGROUND, new Image("maze/pellet.png"), 10)); // Regular pellet prototype
        prototypes.put("powerPellet", new PowerPellet(null, Renderable.Layer.BACKGROUND, new Image("maze/pellet.png"))); // Power pellet prototype
    }

    /**
     * Retrieves a cloned PelletPrototype of the specified type and sets its position.
     *
     * @param type     the type of pellet to clone ("pellet" or "powerPellet")
     * @param position the position to set for the cloned pellet
     * @return a cloned PelletPrototype with the specified position, or null if the type is not found
     */
    public PelletPrototype getPellet(String type, Vector2D position) {
        PelletPrototype prototype = prototypes.get(type);
        return (prototype != null) ? prototype.createCloneWithPosition(position) : null;
    }
}
