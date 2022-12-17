package com.dave.modernchristmas.manager;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dave.modernchristmas.ModernChristmas;
import com.dave.modernchristmas.event.GameStartEvent;
import com.dave.modernchristmas.event.KeyInputEvent;
import com.dave.modernchristmas.object.*;

import com.dave.modernchristmas.screen.GameScreen;
import org.greenrobot.eventbus.Subscribe;


public class ObjectManager {

    private Player player;
    private GameScreen gameScreen;

    private Array<GameObject> objects = new Array<>();


    private int coolDown = 3000;
    private int xVelocity = -15;

    private int reduceCooldownCooldown = 5000;
    private long lastCooldown = System.currentTimeMillis();
    private long lastReduceCooldownCooldown = System.currentTimeMillis();

    private int skeletonDelayTick = 0;

    public ObjectManager(GameScreen gameScreen) {
        ModernChristmas.eventBus.register(this);
        this.gameScreen = gameScreen;

    }

    public void addObject(GameObject gameObject) {
        objects.add(gameObject);
    }

    public void removeObject(GameObject object) {
        objects.removeValue(object, true);
    }

    @Subscribe
    public void onGameStart(GameStartEvent event) {
        player = new Player(gameScreen);
    }

    @Subscribe
    public void onKeyInput(KeyInputEvent event) {
        switch (event.keyCode) {
            case Input.Keys.Z :
                if (event.keyDown) {
                    addObject(new Platform(gameScreen,player.getPosition().x, player.getPosition().y));
//
                }
                break;
            default:
        }
    }

    public void update(SpriteBatch batch, float delta) {
        if (!gameScreen.isGameStarted()) return;

        if (player != null) {
            player.update(batch, delta);
        }

        for (GameObject gameObject : objects) {
            gameObject.update(batch, delta);
        }

        if (System.currentTimeMillis() - lastCooldown > coolDown) {
            lastCooldown = System.currentTimeMillis();
            int offsetX = MathUtils.random(250); //perfect!
            if (skeletonDelayTick == 3) {
                skeletonDelayTick = 0;
                addObject(new Skeleton(gameScreen, 600, 330, xVelocity));
            } else skeletonDelayTick++;

            addObject(new Nutcracker(gameScreen, 600 - offsetX, 330, xVelocity));
        }

        if (System.currentTimeMillis() - lastReduceCooldownCooldown > reduceCooldownCooldown) {
            lastReduceCooldownCooldown = System.currentTimeMillis();
            if (coolDown > 500) {
                coolDown -= 200;
                xVelocity -= 1;
            }

        }


    }
}
