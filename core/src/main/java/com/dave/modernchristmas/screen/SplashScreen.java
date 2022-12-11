package com.dave.modernchristmas.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dave.modernchristmas.AssetFinder;
import com.dave.modernchristmas.AssetManagerResolving;
import com.dave.modernchristmas.ModernChristmas;

import com.dave.modernchristmas.ultralight.UltraLight;
import com.dave.modernchristmas.ultralight.opengl.support.ViewController;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.config.UltralightViewConfig;

public class SplashScreen implements Screen, InputProcessor {
    private AssetManagerResolving assetManager;
    private ViewController viewController;
    private SpriteBatch batch;

    public SplashScreen(ModernChristmas modernChristmas) {
        assetManager = new AssetManagerResolving();
        assetManager.setPathResolving(true);
        AssetFinder finder = new AssetFinder(assetManager);
        finder.load();
        batch = new SpriteBatch();
        modernChristmas.setAssetManager(assetManager);
        Gdx.input.setInputProcessor(this);

        UltralightViewConfig config = new UltralightViewConfig();
        config.isTransparent(true);


        UltralightView view = UltraLight.getRenderer().createView(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), config);

        viewController = new ViewController(UltraLight.getRenderer(), view);
        view.loadURL("https://apple.com");
        view.focus();


    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (assetManager.update()) {

        }

        batch.begin();
        viewController.update();
        viewController.render(batch);
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        viewController.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
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

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
