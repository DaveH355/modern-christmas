package com.dave.modernchristmas.object;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.dave.modernchristmas.AssetManagerResolving;
import com.dave.modernchristmas.Constants;
import com.dave.modernchristmas.GameData;

import com.dave.modernchristmas.ModernChristmas;
import com.dave.modernchristmas.event.AttackEvent;
import com.dave.modernchristmas.event.KeyInputEvent;
import com.dave.modernchristmas.screen.GameScreen;
import org.greenrobot.eventbus.Subscribe;

import java.util.EnumMap;


import static com.dave.modernchristmas.object.AnimationFactory.AnimationState.*;


public class Player implements GameObject {


    private Sprite sprite;
    private Body body;


    private float stateTime = 0;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private float maxVelocityX = 100f;

    private AnimationFactory.AnimationState animationState = IDLE;


    public Player(GameScreen gameScreen) {
        ModernChristmas.eventBus.register(this);

        sprite = new Sprite();
        sprite.setBounds(0, 0, IDLE.xBound, IDLE.yBound);
        sprite.setScale(0.75f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.x = 300 / Constants.PIXELS_PER_METER;
        bodyDef.position.y = 200 / Constants.PIXELS_PER_METER;

        bodyDef.type = BodyDef.BodyType.DynamicBody;


        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10f / Constants.PIXELS_PER_METER, 15f / Constants.PIXELS_PER_METER);
        fdef.shape = shape;

        body = gameScreen.getMapWorldManager().getWorld().createBody(bodyDef);
        body.createFixture(fdef);

    }


    private TextureRegion getActiveFrame() {
        sprite.setBounds(0,0,animationState.xBound, animationState.yBound);

        switch (animationState) {
            case ATTACK_1:
                if (AnimationFactory.getAnimation(ATTACK_1).isAnimationFinished(stateTime)) {
                    stateTime = 0;
                    animationState = IDLE;
                    return getActiveFrame();
                }
                return AnimationFactory.getAnimation(ATTACK_1).getKeyFrame(stateTime);
            case IDLE:
            default:
                return AnimationFactory.getAnimation(IDLE).getKeyFrame(stateTime, true);

        }
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public void update(SpriteBatch batch, float delta) {
        stateTime += delta;

        float xVel = 0;

        if (leftPressed) {
            xVel -= maxVelocityX;
        }
        if (rightPressed) xVel += maxVelocityX;


        body.setLinearVelocity(xVel, body.getLinearVelocity().y);


        sprite.setRegion(getActiveFrame());

        sprite.setPosition(body.getPosition().x - 5, body.getPosition().y - 7);
        sprite.draw(batch);

    }

    @Subscribe
    public void onKeyInput(KeyInputEvent event) {
        switch (event.keyCode) {
            case Input.Keys.LEFT:
                leftPressed = event.keyDown;
                break;
            case Input.Keys.RIGHT:
                rightPressed = event.keyDown;
                break;
            case Input.Keys.UP:
                if (Math.abs(body.getLinearVelocity().y) < 20f) {
                    body.applyLinearImpulse(new Vector2(0, 50f), body.getWorldCenter(), true);

                }
                break;

            case Input.Keys.X:
                if (event.keyDown && animationState != ATTACK_1) {
                    animationState = ATTACK_1;
                    stateTime = 0;
                    body.applyLinearImpulse(new Vector2(0, 30f), body.getWorldCenter(), true);

                    AttackEvent attackEvent = new AttackEvent();
                    attackEvent.positionX = body.getPosition().x;
                    attackEvent.positionY = body.getPosition().y;
                    ModernChristmas.eventBus.post(attackEvent);

                }
                break;
        }
    }



}
