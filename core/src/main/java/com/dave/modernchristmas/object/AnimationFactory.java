package com.dave.modernchristmas.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.dave.modernchristmas.AssetManagerResolving;
import com.dave.modernchristmas.GameData;

import java.util.EnumMap;

public class AnimationFactory {
    private AnimationFactory(){}

    private static final EnumMap<AnimationState, Animation<TextureRegion>> animationMap = new EnumMap<>(AnimationState.class);

    static {
        for (AnimationState state : AnimationState.values()) {
            animationMap.put(state, loadAnimation(state));
        }
    }

    public static Animation<TextureRegion> getAnimation(AnimationState state) {
        return animationMap.get(state);
    }

    private static Animation<TextureRegion> loadAnimation(AnimationState state) {
        AssetManagerResolving assetManager = GameData.getInstance().getGame().getAssetManager();

        Texture sheet = assetManager.get(state.fileName, Texture.class);
        TextureRegion[][] frames = TextureRegion.split(sheet, state.xBound, state.yBound);

        //2d array to array
        Array<TextureRegion> array = new Array<>();
        for (TextureRegion[] frame : frames) {
            for (TextureRegion textureRegion : frame) {
                array.add(textureRegion);
            }
        }

        return new Animation<>(state.frameDuration, array);
    }

    public enum AnimationState {
        PLAYER_IDLE(16, 24, 0.105f,"Meow-Knight_Idle.png" ),
        PLAYER_ATTACK1(32, 35, 0.2f, "Meow-Knight_Attack_3.png");

        public final int xBound;
        public final int yBound;
        public final float frameDuration;
        public final String fileName;
        AnimationState(int xBound, int yBound, float frameDuration, String fileName) {
            this.xBound = xBound;
            this.yBound = yBound;
            this.frameDuration = frameDuration;
            this.fileName = fileName;
        }
    }
}
