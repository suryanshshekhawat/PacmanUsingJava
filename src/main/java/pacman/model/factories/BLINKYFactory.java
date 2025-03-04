package pacman.model.factories;

import javafx.scene.image.Image;
import org.json.simple.JSONObject;
import pacman.ConfigurationParseException;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.ghost.GhostImpl;
import pacman.model.entity.dynamic.ghost.GhostMode;
import pacman.model.entity.dynamic.ghost.Strategy.BLINKYStrategy;
import pacman.model.entity.dynamic.ghost.Strategy.MovementStrategy;
import pacman.model.entity.dynamic.physics.*;
import pacman.model.level.LevelConfigurationReader;

import java.util.Arrays;
import java.util.List;

/**
 * Concrete renderable factory for Blinky Ghost object
 */
public class BLINKYFactory implements RenderableFactory {

    private static final int RIGHT_X_POSITION_OF_MAP = 448;
    private static final int TOP_Y_POSITION_OF_MAP = 16 * 3;
    private static final int BOTTOM_Y_POSITION_OF_MAP = 16 * 34;

    private static final Image BLINKY_IMAGE = new Image("maze/ghosts/blinky.png");

    private static final Image GHOST_IMAGE = BLINKY_IMAGE;

    private final LevelConfigurationReader configReader;

    private char GhostType = RenderableType.BLINKY;

    public BLINKYFactory(JSONObject levelConfigJsonObject) {
        this.configReader = new LevelConfigurationReader(levelConfigJsonObject);
    }

    List<Vector2D> targetCorners = Arrays.asList(
            new Vector2D(0, TOP_Y_POSITION_OF_MAP),
            new Vector2D(RIGHT_X_POSITION_OF_MAP, TOP_Y_POSITION_OF_MAP),
            new Vector2D(0, BOTTOM_Y_POSITION_OF_MAP),
            new Vector2D(RIGHT_X_POSITION_OF_MAP, BOTTOM_Y_POSITION_OF_MAP)
    );

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public Renderable createRenderable(
            Vector2D position
    ) {
        try {
            position = position.add(new Vector2D(4, -4));

            BoundingBox boundingBox = new BoundingBoxImpl(
                    position,
                    GHOST_IMAGE.getHeight(),
                    GHOST_IMAGE.getWidth()
            );

            KinematicState kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                    .setPosition(position)
                    .build();

            MovementStrategy strategy = new BLINKYStrategy();

            return new GhostImpl(
                    GHOST_IMAGE,
                    boundingBox,
                    kinematicState,
                    GhostMode.SCATTER,
                    // top RIGHT corner for BLINKY
                    targetCorners.get(1),
                    this.configReader,
                    strategy,
                    GhostType);

        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid ghost configuration | %s ", e));
        }
    }


}
