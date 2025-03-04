package pacman.model.engine;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pacman.ConfigurationParseException;
import pacman.model.engine.observer.GameState;
import pacman.model.engine.observer.GameStateObserver;
import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.Direction;
import pacman.model.factories.*;
import pacman.model.factories.Prototype.PelletPrototype;
import pacman.model.factories.Prototype.PelletRegistry;
import pacman.model.level.Level;
import pacman.model.level.LevelImpl;
import pacman.model.level.observer.LevelStateObserver;
import pacman.model.maze.Maze;
import pacman.model.maze.MazeCreator;
import pacman.view.keyboard.command.*;
import pacman.model.level.LevelConfigurationReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of GameEngine - responsible for coordinating the Pac-Man model
 */
public class GameEngineImpl implements GameEngine {

    private RenderableFactoryRegistry renderableFactoryRegistry;
    private final List<GameStateObserver> observers;
    private final List<LevelStateObserver> levelStateObservers;
    private Level currentLevel;
    private int numLevels;
    private int currentLevelNo;
    private Maze maze;
    private JSONArray levelConfigs;
    private JSONObject levelConfig;
    private GameState gameState;
    private PelletRegistry pelletRegistry = new PelletRegistry();


    public GameEngineImpl(String configPath) {
        this.currentLevelNo = 0;
        this.observers = new ArrayList<>();
        this.levelStateObservers = new ArrayList<>();

        // Initialize renderableFactoryRegistry before any usage
        this.renderableFactoryRegistry = new RenderableFactoryRegistryImpl();

        // Register factories to renderableFactoryRegistry with dummy config or initialize config directly here if available
        // You can temporarily pass a placeholder JSONObject if config is not available here
        JSONObject placeholderConfig = new JSONObject();
        setupFactoryRegistry(renderableFactoryRegistry, placeholderConfig);

        init(new GameConfigurationReader(configPath));
    }

    // Helper method to setup renderable factories
    private void setupFactoryRegistry(RenderableFactoryRegistry factoryRegistry, JSONObject levelConfig) {
        factoryRegistry.registerFactory(RenderableType.HORIZONTAL_WALL, new WallFactory(RenderableType.HORIZONTAL_WALL));
        factoryRegistry.registerFactory(RenderableType.VERTICAL_WALL, new WallFactory(RenderableType.VERTICAL_WALL));
        factoryRegistry.registerFactory(RenderableType.UP_LEFT_WALL, new WallFactory(RenderableType.UP_LEFT_WALL));
        factoryRegistry.registerFactory(RenderableType.UP_RIGHT_WALL, new WallFactory(RenderableType.UP_RIGHT_WALL));
        factoryRegistry.registerFactory(RenderableType.DOWN_LEFT_WALL, new WallFactory(RenderableType.DOWN_LEFT_WALL));
        factoryRegistry.registerFactory(RenderableType.DOWN_RIGHT_WALL, new WallFactory(RenderableType.DOWN_RIGHT_WALL));
        factoryRegistry.registerFactory(RenderableType.PACMAN, new PacmanFactory());
        factoryRegistry.registerFactory(RenderableType.PINKY, new PINKYFactory(levelConfig));
        factoryRegistry.registerFactory(RenderableType.BLINKY, new BLINKYFactory(levelConfig));
        factoryRegistry.registerFactory(RenderableType.INKY, new INKYFactory(levelConfig));
        factoryRegistry.registerFactory(RenderableType.CLYDE, new CLYDEFactory(levelConfig));
        factoryRegistry.registerFactory(RenderableType.POWER_PELLET, (position) -> pelletRegistry.getPellet("powerPellet", position));
        factoryRegistry.registerFactory(RenderableType.PELLET, (position) -> pelletRegistry.getPellet("pellet", position));
    }

    private void init(GameConfigurationReader gameConfigurationReader) {
        // Set up the map
        String mapFile = gameConfigurationReader.getMapFile();
        MazeCreator mazeCreator = new MazeCreator(mapFile, renderableFactoryRegistry);
        this.maze = mazeCreator.createMaze();
        this.maze.setNumLives(gameConfigurationReader.getNumLives());

        // Get level configurations
        this.levelConfigs = gameConfigurationReader.getLevelConfigs();
        this.numLevels = levelConfigs.size();

        // Validate levels exist in the configuration
        if (levelConfigs.isEmpty()) {
            throw new ConfigurationParseException("No levels found in configuration file.");
        }

        // Initialize levelConfig for the current level
        this.levelConfig = (JSONObject) levelConfigs.get(currentLevelNo);

        // Initialize renderableFactoryRegistry with correct levelConfig
        this.renderableFactoryRegistry = new RenderableFactoryRegistryImpl();
        renderableFactoryRegistry.registerFactory(RenderableType.HORIZONTAL_WALL, new WallFactory(RenderableType.HORIZONTAL_WALL));
        renderableFactoryRegistry.registerFactory(RenderableType.VERTICAL_WALL, new WallFactory(RenderableType.VERTICAL_WALL));
        renderableFactoryRegistry.registerFactory(RenderableType.UP_LEFT_WALL, new WallFactory(RenderableType.UP_LEFT_WALL));
        renderableFactoryRegistry.registerFactory(RenderableType.UP_RIGHT_WALL, new WallFactory(RenderableType.UP_RIGHT_WALL));
        renderableFactoryRegistry.registerFactory(RenderableType.DOWN_LEFT_WALL, new WallFactory(RenderableType.DOWN_LEFT_WALL));
        renderableFactoryRegistry.registerFactory(RenderableType.DOWN_RIGHT_WALL, new WallFactory(RenderableType.DOWN_RIGHT_WALL));
        renderableFactoryRegistry.registerFactory(RenderableType.PACMAN, new PacmanFactory());
        renderableFactoryRegistry.registerFactory(RenderableType.PINKY, new PINKYFactory(levelConfig));
        renderableFactoryRegistry.registerFactory(RenderableType.BLINKY, new BLINKYFactory(levelConfig));
        renderableFactoryRegistry.registerFactory(RenderableType.INKY, new INKYFactory(levelConfig));
        renderableFactoryRegistry.registerFactory(RenderableType.CLYDE, new CLYDEFactory(levelConfig));
        renderableFactoryRegistry.registerFactory(RenderableType.POWER_PELLET, (position) -> pelletRegistry.getPellet("powerPellet", position));
        renderableFactoryRegistry.registerFactory(RenderableType.PELLET, (position) -> pelletRegistry.getPellet("pellet", position));
    }


    @Override
    public List<Renderable> getRenderables() {
        return this.currentLevel.getRenderables();
    }

    @Override
    public void moveUp() {
        currentLevel.moveUp();
    }

    @Override
    public void moveDown() {
        currentLevel.moveDown();
    }

    @Override
    public void moveLeft() {
        currentLevel.moveLeft();
    }

    @Override
    public void moveRight() {
        currentLevel.moveRight();
    }

    @Override
    public void startGame() {
        startLevel();
    }

    private void startLevel() {
        JSONObject levelConfig = (JSONObject) levelConfigs.get(currentLevelNo);
        // reset renderables to starting state
        maze.reset();
        this.currentLevel = new LevelImpl(levelConfig, maze);
        for (LevelStateObserver observer : this.levelStateObservers) {
            this.currentLevel.registerObserver(observer);
        }
        this.setGameState(GameState.READY);
    }

    @Override
    public void tick() {
        if (currentLevel.getNumLives() == 0) {
            handleGameOver();
            return;
        }

        if (currentLevel.isLevelFinished()) {
            handleLevelEnd();
            return;
        }

        maze.checkPelletCollision();

        currentLevel.tick();
    }

    private void handleLevelEnd() {
        if (numLevels - 1 == currentLevelNo) {
            handlePlayerWins();
        } else {
            this.currentLevelNo += 1;

            // remove observers
            for (LevelStateObserver observer : this.levelStateObservers) {
                this.currentLevel.removeObserver(observer);
            }

            startLevel();
        }
    }

    private void handleGameOver() {
        if (gameState != GameState.PLAYER_WIN) {
            setGameState(GameState.GAME_OVER);
            currentLevel.handleGameEnd();
        }
    }

    private void handlePlayerWins() {
        if (gameState != GameState.PLAYER_WIN) {
            setGameState(GameState.PLAYER_WIN);
            currentLevel.handleGameEnd();
        }
    }

    private void setGameState(GameState gameState) {
        this.gameState = gameState;
        notifyObserversWithGameState();
    }

    @Override
    public void registerObserver(GameStateObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void notifyObserversWithGameState() {
        for (GameStateObserver observer : observers) {
            observer.updateGameState(this.gameState);
        }
    }

    @Override
    public void registerLevelStateObserver(LevelStateObserver observer) {
        this.levelStateObservers.add(observer);
    }
}