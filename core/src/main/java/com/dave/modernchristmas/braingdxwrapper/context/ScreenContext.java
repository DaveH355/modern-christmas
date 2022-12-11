package com.dave.modernchristmas.braingdxwrapper.context;


import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dave.modernchristmas.Constants;
import com.dave.modernchristmas.braingdxwrapper.ScreenInputManager;
import com.dave.modernchristmas.braingdxwrapper.ScreenInputManagerImpl;
import de.bitbrain.braingdx.GameSettings;
import de.bitbrain.braingdx.audio.AudioManager;
import de.bitbrain.braingdx.audio.AudioManagerImpl;
import de.bitbrain.braingdx.behavior.BehaviorManager;
import de.bitbrain.braingdx.behavior.BehaviorManagerAdapter;
import de.bitbrain.braingdx.debug.DebugMetric;
import de.bitbrain.braingdx.debug.DebugPanel;
import de.bitbrain.braingdx.event.GameEventManager;
import de.bitbrain.braingdx.event.GameEventManagerImpl;
import de.bitbrain.braingdx.graphics.*;
import de.bitbrain.braingdx.graphics.event.GraphicsSettingsChangeEvent;
import de.bitbrain.braingdx.graphics.pipeline.CombinedRenderPipeline;
import de.bitbrain.braingdx.graphics.pipeline.RenderPipeline;
import de.bitbrain.braingdx.graphics.postprocessing.ShaderManager;
import de.bitbrain.braingdx.graphics.shader.ShaderConfig;
import de.bitbrain.braingdx.tweens.SharedTweenManager;
import de.bitbrain.braingdx.util.Resizeable;
import de.bitbrain.braingdx.world.GameWorld;
//https://stackoverflow.com/questions/32751181/possible-memory-leak-in-java-arraylist-iterator
public class ScreenContext implements Disposable, Resizeable  {

    private final GameWorld gameWorld;
    private final BehaviorManager behaviorManager;
    private final Stage stage;
    private final Stage debugStage;
    private final ScreenInputManagerImpl inputManager;
    private final TweenManager tweenManager = SharedTweenManager.getInstance();
    private final GameObjectRenderManager renderManager;
    private final GameEventManager eventManager;
    private final AudioManager audioManager;
    private final GameSettings settings;
    private final ShaderManager shaderManager;
    private final GameCamera gameCamera;

    private Color backgroundColor = Color.BLACK.cpy();
    private final RenderPipeline renderPipeline;
    private boolean paused;

    private boolean debug;
    private final DebugPanel debugPanel;


    public ScreenContext() {
        this.eventManager = new GameEventManagerImpl();
        this.inputManager = new ScreenInputManagerImpl();
        Gdx.input.setInputProcessor(inputManager.getMultiplexer());
        this.settings = new GameSettings(eventManager);
        ShaderConfig shaderConfig = new ShaderConfig();
        this.shaderManager = new ShaderManager(shaderConfig, eventManager, settings.getGraphics());
        this.gameWorld = new GameWorld();
        this.gameCamera = new VectorGameCamera(new OrthographicCamera(), this.gameWorld);
        this.gameWorld.setCamera(gameCamera);
        BatchResolver<?>[] batchResolvers = new BatchResolver[]{new SpriteBatchResolver(gameCamera.getInternalCamera())};
        this.renderPipeline = new CombinedRenderPipeline(shaderConfig, batchResolvers);
        this.behaviorManager = new BehaviorManager(gameWorld);
        this.stage = new Stage(new ExtendViewport(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT,new OrthographicCamera()));
        this.debugStage = new Stage(stage.getViewport(), stage.getBatch());
        this.debugPanel = new DebugPanel();
        debugStage.addActor(debugPanel);
        this.audioManager = new AudioManagerImpl(//
                gameCamera,//
                tweenManager,//
                gameWorld,//
                behaviorManager//
        );
        this.renderManager = new GameObjectRenderManager(this.gameWorld, batchResolvers);
        wire();
    }


    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public Stage getStage() {
        return stage;
    }

    public Stage getDebugStage() {
        return debugStage;
    }

    public DebugPanel getDebugPanel() {
        return debugPanel;
    }

    public TweenManager getTweenManager() {
        return tweenManager;
    }

    public BehaviorManager getBehaviorManager() {
        return behaviorManager;
    }



    public AudioManager getAudioManager() {
        return audioManager;
    }

    public GameEventManager getEventManager() {
        return eventManager;
    }

    public GameSettings getSettings() {
        return settings;
    }


    public ShaderManager getShaderManager() {
        return shaderManager;
    }

    public GameCamera getGameCamera() {
        return gameCamera;
    }


    @Override
    public void dispose() {
        gameWorld.clear();
        stage.dispose();
        debugStage.dispose();
        inputManager.dispose();
        tweenManager.killAll();
        eventManager.clear();
        renderPipeline.dispose();
        renderManager.dispose();
    }


    public void render(float delta) {
        inputManager.update(delta);
        behaviorManager.update(paused ? 0f : delta);
        tweenManager.update(delta);
        gameCamera.update(delta);
        gameWorld.update(paused ? 0f : delta);
        stage.act(delta);
        debugStage.act(delta);
        renderPipeline.render(delta);
    }

    public GameObjectRenderManager getRenderManager() {
        return renderManager;
    }

    public RenderPipeline getRenderPipeline() {
        return renderPipeline;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public ScreenInputManager getInputManager() {
        return this.inputManager;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setDebug(boolean enabled) {
        this.debug = enabled;
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    @Override
    public void resize(int width, int height) {
        gameCamera.resize(width, height);
        stage.getViewport().update(width, height, true);
        debugStage.getViewport().update(width, height, true);
        renderPipeline.resize(width, height);
        eventManager.publish(new GraphicsSettingsChangeEvent());
    }


    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    //do not simplify to lambda. Keep as is for clarity
    private void wire() {
        gameWorld.addListener(new BehaviorManagerAdapter(behaviorManager));

        // Setup Debug UI
        debugPanel.addMetric("fps", new DebugMetric() {

            @Override
            public String getCurrentValue() {
                return String.valueOf(Gdx.graphics.getFramesPerSecond());
            }
        });
        debugPanel.addMetric("total game objects", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return String.valueOf(getGameWorld().size());
            }
        });
        debugPanel.addMetric("updateable game objects", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return String.valueOf(getGameWorld().getObjects(null, true).size);
            }
        });
        debugPanel.addMetric("global behaviors", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return String.valueOf(getBehaviorManager().getGlobalCount());
            }
        });
        debugPanel.addMetric("local behaviors", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return String.valueOf(getBehaviorManager().getLocalCount());
            }
        });
        debugPanel.addMetric("active tweens", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return String.valueOf(getTweenManager().size());
            }
        });
        debugPanel.addMetric("camera position", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return "x=" + gameCamera.getLeft() + ", y=" + gameCamera.getTop();
            }
        });
        debugPanel.addMetric("camera viewport (scaled)", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return "width=" + gameCamera.getScaledCameraWidth() + ", height=" + gameCamera.getScaledCameraHeight();
            }
        });
        debugPanel.addMetric("camera viewport (unscaled)", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return "width=" + gameCamera.getUnscaledCameraWidth() + ", height=" + gameCamera.getUnscaledCameraHeight();
            }
        });
        debugPanel.addMetric("camera zoom", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                if (gameCamera.getInternalCamera() instanceof OrthographicCamera) {
                    return String.valueOf(((OrthographicCamera)gameCamera.getInternalCamera()).zoom);
                } else {
                    return "N/A";
                }
            }
        });
        debugPanel.addMetric("memory usage", new DebugMetric() {
            @Override
            public String getCurrentValue() {
                return Gdx.app.getJavaHeap() / 1000000 + " MB";
            }
        });
    }
}
