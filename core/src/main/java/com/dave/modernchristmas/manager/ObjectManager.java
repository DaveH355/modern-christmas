package com.dave.modernchristmas.manager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dave.modernchristmas.ModernChristmas;
import com.dave.modernchristmas.event.GameStartEvent;
import com.dave.modernchristmas.object.Tree;

import com.dave.modernchristmas.screen.GameScreen;
import org.greenrobot.eventbus.Subscribe;

public class ObjectManager {

    private Tree tree;
    private GameScreen gameScreen;

    public ObjectManager(GameScreen gameScreen) {
        ModernChristmas.eventBus.register(this);
        this.gameScreen = gameScreen;

    }

    @Subscribe
    public void onGameStart(GameStartEvent event) {
        tree = new Tree(gameScreen);
    }

    public void update(SpriteBatch batch, float delta) {
        if (tree != null) {
            tree.update(batch, delta);
        }

    }
}
