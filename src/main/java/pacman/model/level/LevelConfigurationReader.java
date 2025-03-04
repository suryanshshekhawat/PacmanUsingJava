package pacman.model.level;

import org.json.simple.JSONObject;
import pacman.model.entity.dynamic.ghost.GhostMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to read JSONObject to retrieve level configuration details
 */
public class LevelConfigurationReader {

    private final JSONObject levelConfiguration;

    public LevelConfigurationReader(JSONObject levelConfiguration) {

        this.levelConfiguration = levelConfiguration;
        System.out.println("Level configuration loaded: " + levelConfiguration.toJSONString());

    }

    /**
     * Retrieves the player's speed for the level
     *
     * @return the player's speed for the level
     */
    public double getPlayerSpeed() {
        Object speedValue = levelConfiguration.get("pacmanSpeed");
        return speedValue != null ? ((Number) speedValue).doubleValue() : 1.0; // Default to 1.0 if null
    }

    /**
     * Retrieves the lengths of the ghost modes in seconds
     *
     * @return the lengths of the ghost modes in seconds
     */
    public Map<GhostMode, Integer> getGhostModeLengths() {
        Map<GhostMode, Integer> ghostModeLengths = new HashMap<>();
        JSONObject modeLengthsObject = (JSONObject) levelConfiguration.get("modeLengths");

        if (modeLengthsObject != null) {
            Object chaseLength = modeLengthsObject.get("chase");
            ghostModeLengths.put(GhostMode.CHASE, chaseLength != null ? ((Number) chaseLength).intValue() : 20);

            Object scatterLength = modeLengthsObject.get("scatter");
            ghostModeLengths.put(GhostMode.SCATTER, scatterLength != null ? ((Number) scatterLength).intValue() : 7);

            Object frightenedLength = modeLengthsObject.get("frightened");
            ghostModeLengths.put(GhostMode.FRIGHTENED, frightenedLength != null ? ((Number) frightenedLength).intValue() : 10);
        } else {
            // Default values if modeLengthsObject is null
            ghostModeLengths.put(GhostMode.CHASE, 20); // Default 20 seconds
            ghostModeLengths.put(GhostMode.SCATTER, 7); // Default 7 seconds
            ghostModeLengths.put(GhostMode.FRIGHTENED, 10); // Default 10 seconds
        }

        return ghostModeLengths;
    }

    /**
     * Retrieves the speeds of the ghosts for each ghost mode
     *
     * @return the speeds of the ghosts for each ghost mode
     */
    public Map<GhostMode, Double> getGhostSpeeds() {
        Map<GhostMode, Double> ghostSpeeds = new HashMap<>();
        JSONObject ghostSpeed = (JSONObject) levelConfiguration.get("ghostSpeed");

        if (ghostSpeed != null) {
            Object chaseSpeed = ghostSpeed.get("chase");
            ghostSpeeds.put(GhostMode.CHASE, chaseSpeed != null ? ((Number) chaseSpeed).doubleValue() : 1.0);

            Object scatterSpeed = ghostSpeed.get("scatter");
            ghostSpeeds.put(GhostMode.SCATTER, scatterSpeed != null ? ((Number) scatterSpeed).doubleValue() : 0.8);

            Object frightenedSpeed = ghostSpeed.get("frightened");
            ghostSpeeds.put(GhostMode.FRIGHTENED, frightenedSpeed != null ? ((Number) frightenedSpeed).doubleValue() : 0.5);
        } else {
            // Default values if ghostSpeed is null
            ghostSpeeds.put(GhostMode.CHASE, 1.0); // Default speed for chase mode
            ghostSpeeds.put(GhostMode.SCATTER, 0.8); // Default speed for scatter mode
            ghostSpeeds.put(GhostMode.FRIGHTENED, 0.5); // Default speed for frightened mode
        }

        return ghostSpeeds;
    }
}
