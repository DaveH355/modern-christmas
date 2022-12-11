package com.dave.modernchristmas.braingdxwrapper;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;
import de.bitbrain.braingdx.util.Updateable;

public class ScreenInputManagerImpl implements Updateable, Disposable, ScreenInputManager {

    private final InputMultiplexer inputMultiplexer;

    public ScreenInputManagerImpl() {
        this.inputMultiplexer = new InputMultiplexer();
    }

    public InputMultiplexer getMultiplexer() {
        return inputMultiplexer;
    }


    @Override
    public void register(InputProcessor inputAdapter) {
        inputMultiplexer.addProcessor(inputAdapter);
    }

    @Override
    public void clear() {
        inputMultiplexer.clear();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void update(float delta) {
        for (InputProcessor processor : inputMultiplexer.getProcessors()) {
            if (processor instanceof Updateable) {
                ((Updateable) processor).update(delta);
            }
        }
    }
}
