package com.dave.modernchristmas.object;

import com.badlogic.gdx.Input;
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
import com.dave.modernchristmas.ModernChristmas;
import com.dave.modernchristmas.event.KeyInputEvent;

import com.dave.modernchristmas.screen.GameScreen;
import org.greenrobot.eventbus.Subscribe;

public class Tree {


    private Sprite sprite;
    private Body body;


    public Tree(GameScreen gameScreen) {


        AssetManagerResolving assetManager = GameData.getInstance().getGame().getAssetManager();

        Texture texture = assetManager.get("treesmall.png", Texture.class);

        sprite = new Sprite(texture);
        sprite.setScale(0.25f);



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

    public void update(SpriteBatch batch, float delta) {
        sprite.setPosition(body.getPosition().x - 20, body.getPosition().y - 35);
        sprite.draw(batch);

    }

}
