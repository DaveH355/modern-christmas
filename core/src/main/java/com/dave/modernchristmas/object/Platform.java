package com.dave.modernchristmas.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.dave.modernchristmas.AssetManagerResolving;
import com.dave.modernchristmas.Constants;
import com.dave.modernchristmas.GameData;

import com.dave.modernchristmas.screen.GameScreen;

public class Platform implements GameObject{
    private Sprite sprite;
    private Body body;
    private int lifeTime = 5000;
    private final long creation = System.currentTimeMillis();
    private GameScreen screen;

    public Platform(GameScreen screen, float positionX, float positionY) {
        this.screen = screen;

        AssetManagerResolving assetManager = GameData.getInstance().getGame().getAssetManager();
        sprite = new Sprite(assetManager.get("glass.png", Texture.class));
        sprite.setScale(0.15f);


        BodyDef bodyDef = new BodyDef();
        bodyDef.position.x = positionX;
        bodyDef.position.y = positionY - 60;

        bodyDef.type = BodyDef.BodyType.StaticBody;


        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5f / Constants.PIXELS_PER_METER, 5f / Constants.PIXELS_PER_METER);
        fdef.shape = shape;

        body = screen.getMapWorldManager().getWorld().createBody(bodyDef);
        body.createFixture(fdef);
    }

    @Override
    public void update(SpriteBatch batch, float delta) {
        if (System.currentTimeMillis() - creation > lifeTime) {
            screen.getMapWorldManager().getWorld().destroyBody(body);
            screen.getObjectManager().removeObject(this);
            return;
        }

        sprite.setPosition(body.getPosition().x - 30, body.getPosition().y - 32);



        sprite.draw(batch);
    }
}
