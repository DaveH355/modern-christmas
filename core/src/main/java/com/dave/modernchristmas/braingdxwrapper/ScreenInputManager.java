package com.dave.modernchristmas.braingdxwrapper;

import com.badlogic.gdx.InputProcessor;


public interface ScreenInputManager{
    void register(InputProcessor inputAdapter);
    void clear();
}
