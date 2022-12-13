package com.dave.modernchristmas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.dave.modernchristmas.screen.EmptyLoadScreen;
import com.dave.modernchristmas.screen.GameScreen;
import com.dave.modernchristmas.ultralight.UltraLight;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ModernChristmas extends Game {

	public static EventBus eventBus =  EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).build();
	private AssetManagerResolving assetManager;
	private Skin skin;
	@Override
	public void create() {
		UltraLight.init();
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

		GameData.getInstance().setGame(this);
		setScreen(new EmptyLoadScreen(this));
	}

	public void setAssetManager(AssetManagerResolving assetManager) {
		this.assetManager = assetManager;
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}

    public AssetManagerResolving getAssetManager() {
		return assetManager;
    }
}