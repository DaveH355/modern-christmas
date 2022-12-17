package com.dave.modernchristmas.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dave.modernchristmas.Constants;
import com.dave.modernchristmas.GameData;
import com.dave.modernchristmas.ModernChristmas;

import com.dave.modernchristmas.event.EnemyAtTreeEvent;
import com.dave.modernchristmas.event.GameStartEvent;
import com.dave.modernchristmas.event.KeyInputEvent;
import com.dave.modernchristmas.manager.MapWorldManager;
import com.dave.modernchristmas.manager.ObjectManager;
import com.dave.modernchristmas.ultralight.UltraLight;

import com.dave.modernchristmas.ultralight.js.JavaScriptUse;
import com.dave.modernchristmas.ultralight.util.ViewController;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.config.UltralightViewConfig;
import com.labymedia.ultralight.javascript.JavascriptContext;
import com.labymedia.ultralight.javascript.JavascriptEvaluationException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


import java.util.ArrayList;
import java.util.List;

public class GameScreen implements InputProcessor, Screen {
    private ViewController viewController;
    private SpriteBatch batch;
    private SpriteBatch hudBatch;
    private ModernChristmas game;
    private List<ViewController> activeViews = new ArrayList<>();
    private boolean gameStarted = false;

    private ObjectManager objectManager = new ObjectManager(this);

    private MapWorldManager mapWorldManager;
    private OrthographicCamera camera;
    private Viewport viewport;


    public GameScreen(ModernChristmas modernChristmas) {
        this.game = modernChristmas;
        ModernChristmas.eventBus.register(this);
        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        //game load
        camera = new OrthographicCamera();
        camera.zoom = 0.40f;
        camera.position.y = 100;
        camera.position.x = 170;

        viewport = new ExtendViewport(Constants.DEFAULT_WIDTH / Constants.PIXELS_PER_METER, Constants.DEFAULT_HEIGHT / Constants.PIXELS_PER_METER, camera);
        mapWorldManager = new MapWorldManager(camera);


        //html load
        UltralightViewConfig config = new UltralightViewConfig();
        config.isTransparent(true);

        UltralightView view = UltraLight.getRenderer().createView(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), config);
        view.loadURL("file://ultralight/dist/index.html");
        view.focus();
        viewController = new ViewController(UltraLight.getRenderer(), view);

        activeViews.add(viewController);

        JavascriptContext context = viewController.getJSBridge().getContextLock().getContext();
        viewController.getJSBridge().setContext(this, "gameScreen", context);
    }
    @Override
    public void show() {

    }

    @JavaScriptUse
    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
        ModernChristmas.eventBus.post(new GameStartEvent());

    }

    @JavaScriptUse
    public void restart() {
        ModernChristmas.eventBus = EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).build();
        game.setScreen(new GameScreen(game));
        GameData.getInstance().setScore(0);
    }

    @Subscribe
    public void enemyAtTree(EnemyAtTreeEvent event) {
        if (gameStarted) {
            endGame();
        }
    }

    public void endGame() {
        gameStarted = false;
        try {
            viewController.getView().evaluateScript("window.gameOver()");
        } catch (JavascriptEvaluationException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHealth(String health) {
        try {
            viewController.getView().evaluateScript("window.setHealth(\"{}\")".replace("{}", health));
        } catch (JavascriptEvaluationException e) {
            throw new RuntimeException(e);
        }
    }

    public void warnHealth() {
        try {
            viewController.getView().evaluateScript("window.warnHealth()");
        } catch (JavascriptEvaluationException e) {
            throw new RuntimeException(e);
        }
    }

    public void setScore(int score) {
        try {
            viewController.getView().evaluateScript("window.setScore(\"{}\")".replace("{}", score + ""));
        } catch (JavascriptEvaluationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(36/255f,67/255f,114/255f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        objectManager.update(batch, delta);
        mapWorldManager.update(delta);
        batch.end();

        //yay html
        hudBatch.begin();
        for (ViewController controller : activeViews) {
            controller.update();
            controller.render(hudBatch);
        }
        hudBatch.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        getMapWorldManager().getWorld().dispose();
        batch.dispose();
        hudBatch.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        KeyInputEvent event = new KeyInputEvent();
        event.keyCode = keycode;
        event.keyDown = true;
        ModernChristmas.eventBus.post(event);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        KeyInputEvent event = new KeyInputEvent();
        event.keyCode = keycode;
        event.keyDown = false;
        ModernChristmas.eventBus.post(event);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        viewController.onMouseDown(screenX, screenY, button);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        viewController.onMouseUp(screenX, screenY, button);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        viewController.onMouseMove(screenX, screenY);
        return false;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public MapWorldManager getMapWorldManager() {
        return mapWorldManager;
    }


    public ObjectManager getObjectManager() {
        return objectManager;
    }
}
