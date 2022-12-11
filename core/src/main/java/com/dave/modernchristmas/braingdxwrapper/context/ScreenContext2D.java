package com.dave.modernchristmas.braingdxwrapper.context;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dave.modernchristmas.Constants;
import com.dave.modernchristmas.braingdxwrapper.StageDebugRenderLayer;
import de.bitbrain.braingdx.debug.DebugMetric;
import de.bitbrain.braingdx.event.GameEventRouter;
import de.bitbrain.braingdx.graphics.lighting.LightingManager;
import de.bitbrain.braingdx.graphics.lighting.LightingManagerImpl;
import de.bitbrain.braingdx.graphics.lighting.LightingManagerRenderLayer;
import de.bitbrain.braingdx.graphics.particles.ParticleManager;
import de.bitbrain.braingdx.graphics.particles.ParticleManagerImpl;
import de.bitbrain.braingdx.graphics.particles.ParticleManagerRenderLayer;
import de.bitbrain.braingdx.graphics.pipeline.RenderLayer2D;
import de.bitbrain.braingdx.graphics.pipeline.RenderPipeline;
import de.bitbrain.braingdx.graphics.pipeline.layers.ColoredRenderLayer;
import de.bitbrain.braingdx.graphics.pipeline.layers.GameObjectRenderLayer;
import de.bitbrain.braingdx.graphics.pipeline.layers.RenderPipeIds;
import de.bitbrain.braingdx.graphics.pipeline.layers.StageRenderLayer;
import de.bitbrain.braingdx.physics.PhysicsManager;
import de.bitbrain.braingdx.physics.PhysicsManagerImpl;
import de.bitbrain.braingdx.tmx.*;
import de.bitbrain.braingdx.tmx.events.TmxAudioConfigurer;
import de.bitbrain.braingdx.tmx.events.TmxLightingConfigurer;
public class ScreenContext2D extends ScreenContext {

    private final Stage worldStage;
    private final LightingManagerImpl lightingManager;
    private final ParticleManagerImpl particleManager;
    private final World boxWorld;
    private final TiledMapManager tiledMapManager;
    private final PhysicsManagerImpl physicsManager;
    private final ColoredRenderLayer coloredRenderLayer;
    private final GameEventRouter tiledMapEventRouter;


    public ScreenContext2D() {
        super();
        coloredRenderLayer = new ColoredRenderLayer();
        particleManager = new ParticleManagerImpl(getBehaviorManager(), getSettings().getGraphics());
        worldStage = new Stage(new FitViewport(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, getGameCamera().getInternalCamera()));
        boxWorld = new World(Vector2.Zero, true);
        physicsManager = new PhysicsManagerImpl(
                boxWorld,
                getGameWorld(),
                getBehaviorManager()
        );
        lightingManager = new LightingManagerImpl(
                new RayHandler(boxWorld),
                getBehaviorManager(),
                (OrthographicCamera) getGameCamera().getInternalCamera()
        );
        tiledMapEventRouter = new GameEventRouter(
                getEventManager(),
                getGameWorld(),
                new TiledMapInfoExtractor()
        );
        tiledMapManager = new TiledMapManagerImpl(
                getGameWorld(),
                getEventManager(),
                new TiledMapContextFactory(
                        getRenderManager(),
                        getGameWorld(),
                        getEventManager(),
                        tiledMapEventRouter,
                        getBehaviorManager(),
                        physicsManager
                )
        );
        configurePipeline(getRenderPipeline(), this);
        wire();
    }

    public Stage getWorldStage() {
        return worldStage;
    }

    public World getBox2DWorld() {
        return boxWorld;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public LightingManager getLightingManager() {
        return lightingManager;
    }

    public TiledMapManager getTiledMapManager() {
        return tiledMapManager;
    }

    @Override
    public void dispose() {
        super.dispose();
        worldStage.dispose();
        particleManager.dispose();
        physicsManager.dispose();
        lightingManager.dispose();
    }



    @Override
    public void render(float delta) {
        physicsManager.update(delta);
        worldStage.act(delta);
        super.render(delta);
    }

    @Override
    public void setBackgroundColor(Color color) {
        super.setBackgroundColor(color);
        coloredRenderLayer.setColor(color);
        getRenderPipeline().put(RenderPipeIds.BACKGROUND, coloredRenderLayer);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        worldStage.getViewport().update(width, height, true);
        lightingManager.resize(width, height);
    }


    public PhysicsManager getPhysicsManager() {
        return physicsManager;
    }

    private void configurePipeline(RenderPipeline pipeline, ScreenContext2D context) {
        pipeline.put(RenderPipeIds.BACKGROUND, new RenderLayer2D() {
            @Override
            public void render(Batch batch, float delta) {
            }
        });
        pipeline.put(RenderPipeIds.FOREGROUND, new RenderLayer2D() {
            @Override
            public void render(Batch batch, float delta) {
            }
        });
        pipeline.put(RenderPipeIds.WORLD, new GameObjectRenderLayer(context.getRenderManager()));
        pipeline.put(RenderPipeIds.LIGHTING, new LightingManagerRenderLayer(lightingManager));
        pipeline.put(RenderPipeIds.PARTICLES, new ParticleManagerRenderLayer(particleManager));
        pipeline.put(RenderPipeIds.WORLD_UI, new StageRenderLayer(context.getWorldStage()));
        pipeline.put(RenderPipeIds.UI, new StageRenderLayer(context.getStage()));
        pipeline.put(RenderPipeIds.DEBUG, new StageDebugRenderLayer(context));
    }

    private void wire() {

        getBehaviorManager().apply(tiledMapEventRouter);

        // TiledMap features
        getEventManager().register(new TmxAudioConfigurer(getAudioManager()), TiledMapEvents.OnLoadGameObjectEvent.class);
        getEventManager().register(new TmxLightingConfigurer(getLightingManager()), TiledMapEvents.OnLoadGameObjectEvent.class);

        getDebugPanel().addMetric("light count", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return String.valueOf(lightingManager.size());
            }
        });
        getDebugPanel().addMetric("box2d body count", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return String.valueOf(physicsManager.getPhysicsWorld().getBodyCount());
            }
        });
        getDebugPanel().addMetric("particle effect count", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return String.valueOf(particleManager.getTotalEffectCount());
            }
        });
    }
}
