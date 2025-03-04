package pacman.model.factories;

import pacman.model.entity.Renderable;
import pacman.model.entity.dynamic.physics.Vector2D;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for delegating createRenderable requests to the appropriate registered concrete factory.
 */
public class RenderableFactoryRegistryImpl implements RenderableFactoryRegistry {

    private final Map<Character, RenderableFactory> factoryRegistry = new HashMap<>();

    @Override
    public void registerFactory(
            char renderableType,
            RenderableFactory factory
    ) {
        factoryRegistry.put(renderableType, factory);
    }

    @Override
    public Renderable createRenderable(
            char renderableType,
            Vector2D position
    ) {
        if (!factoryRegistry.containsKey(renderableType)) {
            return null;
        }

        RenderableFactory factory = factoryRegistry.get(renderableType);
        return factory.createRenderable(position);
    }
}



