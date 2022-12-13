package com.dave.modernchristmas;

import com.badlogic.gdx.Game;

public class GameData {
    private static GameData instance;

    private GameData() {
    }

    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }


    public ModernChristmas getGame() {
        return game;
    }

    public void setGame(ModernChristmas game) {
        this.game = game;
    }

    private ModernChristmas game;
}
