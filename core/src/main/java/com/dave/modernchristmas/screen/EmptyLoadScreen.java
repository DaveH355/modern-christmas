package com.dave.modernchristmas.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dave.modernchristmas.AssetFinder;
import com.dave.modernchristmas.AssetManagerResolving;
import com.dave.modernchristmas.ModernChristmas;

import com.dave.modernchristmas.ultralight.util.ViewController;

public class EmptyLoadScreen implements Screen, InputProcessor {
    private AssetManagerResolving assetManager;
    private ViewController viewController;
    private SpriteBatch batch;
    private ModernChristmas game;


    public EmptyLoadScreen(ModernChristmas modernChristmas) {
        this.game = modernChristmas;
        assetManager = new AssetManagerResolving();
        assetManager.setPathResolving(true);
        AssetFinder finder = new AssetFinder(assetManager);
        finder.load();

        modernChristmas.setAssetManager(assetManager);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0/255f, 0/255f, 0/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (assetManager.update()) {
            game.setScreen(new GameScreen(game));
        }

    }

    @Override
    public void resize(int width, int height) {

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
