package com.dave.modernchristmas.braingdxwrapper;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.dave.modernchristmas.braingdxwrapper.context.ScreenContext;
import de.bitbrain.braingdx.graphics.pipeline.layers.StageRenderLayer;

public class StageDebugRenderLayer extends StageRenderLayer {
    private final ScreenContext context;

    public StageDebugRenderLayer(ScreenContext context) {
        super(context.getDebugStage());
        this.context = context;
    }

    @Override
    public void render(Batch batch, float delta) {
        if (context.isDebugEnabled()) {
            super.render(batch, delta);
        }
    }
}
