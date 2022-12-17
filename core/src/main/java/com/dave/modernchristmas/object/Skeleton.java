package com.dave.modernchristmas.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.dave.modernchristmas.AssetManagerResolving;
import com.dave.modernchristmas.Constants;
import com.dave.modernchristmas.GameData;
import com.dave.modernchristmas.ModernChristmas;
import com.dave.modernchristmas.event.AttackEvent;
import com.dave.modernchristmas.event.EnemyAtTreeEvent;
import com.dave.modernchristmas.screen.GameScreen;
import org.greenrobot.eventbus.Subscribe;

import static com.dave.modernchristmas.object.AnimationFactory.AnimationState.SKELETON;

public class Skeleton implements GameObject {
    private Sprite sprite;
    private Body body;

    private float stateTime = 0;
    private GameScreen screen;
    private float damageRange = 25f;
    private boolean shouldKill = false;
    private Sound deathSound;
    private int speed;

    public Skeleton(GameScreen screen, float positionX, float positionY, int speed) {
        this.speed = speed;
        ModernChristmas.eventBus.register(this);
        AssetManagerResolving assetManager = GameData.getInstance().getGame().getAssetManager();

        deathSound = assetManager.get("death.wav", Sound.class);
        this.screen = screen;


        sprite = new Sprite();
        sprite.setBounds(0, 0, SKELETON.xBound, SKELETON.yBound );
        sprite.setScale(0.1f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.x = positionX / Constants.PIXELS_PER_METER;
        bodyDef.position.y = positionY / Constants.PIXELS_PER_METER;

        bodyDef.type = BodyDef.BodyType.DynamicBody;


        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10f / Constants.PIXELS_PER_METER, 20f / Constants.PIXELS_PER_METER);
        fdef.shape = shape;

        body = screen.getMapWorldManager().getWorld().createBody(bodyDef);
        body.setUserData("nutcracker");
        Fixture fixture = body.createFixture(fdef);
        fixture.setUserData(this);

    }

    @Override
    public void update(SpriteBatch batch, float delta) {
        if (shouldKill) {
            kill();
            return;
        }
        stateTime += delta;
        body.setLinearVelocity(speed, body.getLinearVelocity().y);
        sprite.setRegion(AnimationFactory.getAnimation(SKELETON).getKeyFrame(stateTime, true));
        sprite.setPosition(body.getPosition().x - 25 , body.getPosition().y - 25);
        sprite.flip(true, false);
        sprite.draw(batch);
    }

    private void kill() {
        deathSound.play(0.25f);
        GameData.getInstance().setScore(GameData.getInstance().getScore() + 1);
        screen.setScore(GameData.getInstance().getScore());
        screen.getMapWorldManager().getWorld().destroyBody(body);
        screen.getObjectManager().removeObject(this);
    }

    @Subscribe
    public void onAttack(AttackEvent event) {

        if (Math.abs(event.positionX - body.getPosition().x) <= damageRange && Math.abs(event.positionY - body.getPosition().y) <= damageRange ) {
            shouldKill = true;
        }
    }

    @Subscribe
    public void reachedTree(EnemyAtTreeEvent event) {
        if (event.enemyBody == body) {
            shouldKill = true;
        }
    }
}
