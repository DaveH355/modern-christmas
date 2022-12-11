package com.dave.modernchristmas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.dave.modernchristmas.screen.SplashScreen;
import com.dave.modernchristmas.ultralight.UltraLight;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ModernChristmas extends Game {

	private AssetManager assetManager;
	@Override
	public void create() {
		UltraLight.init();
		setScreen(new SplashScreen(this));
	}

	public void setAssetManager(AssetManager assetManager) {
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
}