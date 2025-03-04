package pacman.model.factories.Prototype;

import javafx.scene.image.Image;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.BoundingBox;
import pacman.model.entity.dynamic.physics.BoundingBoxImpl;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.staticentity.collectable.Pellet;

/**
 * The PowerPellet class extends the Pellet class and represents a special type of pellet
 * in the Pac-Man game that grants the player additional abilities when collected.
 * This class implements the Prototype design pattern, allowing power pellets to be cloned
 * with specific positions.
 */
public class PowerPellet extends Pellet {

    /**
     * The PowerPellet class extends the Pellet class and represents a special type of pellet
     * in the Pac-Man game that grants the player additional abilities when collected.
     * This class implements the Prototype design pattern, allowing power pellets to be cloned
     * with specific positions.
     */
    public PowerPellet(BoundingBox boundingBox, Renderable.Layer layer, Image image) {
        super(boundingBox, layer, image, 50);
    }

    /**
     * Creates a clone of the PowerPellet with a specified position.
     * The position is adjusted by moving the pellet 8 pixels up and to the left,
     * and the size is doubled by scaling the width and height of the bounding box.
     *
     * @param position the new position for the cloned power pellet
     * @return a new PowerPellet instance with the adjusted position and size
     */
    @Override
    public PowerPellet createCloneWithPosition(Vector2D position) {
        Vector2D adjustedPosition = new Vector2D(position.getX() - 8, position.getY() - 8);

        double width = getImage().getWidth() * 2;
        double height = getImage().getHeight() * 2;

        BoundingBox boundingBox = new BoundingBoxImpl(adjustedPosition, height, width);
        return new PowerPellet(boundingBox, getLayer(), getImage());
    }

    /**
     * Indicates that this pellet is a power pellet.
     *
     * @return true, as this is a power pellet
     */
    @Override
    public boolean isPowerPellet() {
        return true;
    }
}
