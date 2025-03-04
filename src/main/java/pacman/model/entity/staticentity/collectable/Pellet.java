package pacman.model.entity.staticentity.collectable;

import javafx.scene.image.Image;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.BoundingBox;
import pacman.model.entity.dynamic.physics.BoundingBoxImpl;
import pacman.model.entity.dynamic.physics.Vector2D;
import pacman.model.entity.staticentity.StaticEntityImpl;
import pacman.model.factories.Prototype.PelletPrototype;

/**
 * Represents the Pellet in Pac-Man game
 */
public class Pellet extends StaticEntityImpl implements Collectable, PelletPrototype {

    private final int points;
    private boolean isCollectable;
    private final boolean isPowerPellet;

    public Pellet(BoundingBox boundingBox, Renderable.Layer layer, Image image, int points) {
        super(boundingBox, layer, image);
        this.points = points;
        this.isCollectable = true;
        this.isPowerPellet = points >= 50; // Assuming power pellets have higher points, e.g., 50
    }

    @Override
    public Pellet createCloneWithPosition(Vector2D position) {
        BoundingBox boundingBox = new BoundingBoxImpl(position, getImage().getHeight(), getImage().getWidth());
        return new Pellet(boundingBox, getLayer(), getImage(), this.points);
    }

    @Override
    public void collect() {
        this.isCollectable = false;
        setLayer(Layer.INVISIBLE);
    }

    @Override
    public void reset() {
        this.isCollectable = true;
        setLayer(Layer.BACKGROUND);
    }

    @Override
    public boolean isCollectable() {
        return this.isCollectable;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    public boolean isPowerPellet() {
        return isPowerPellet;
    }
}
