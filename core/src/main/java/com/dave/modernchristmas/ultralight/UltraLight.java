package com.dave.modernchristmas.ultralight;

import com.badlogic.gdx.Gdx;
import com.labymedia.ultralight.UltralightJava;
import com.labymedia.ultralight.UltralightLoadException;
import com.labymedia.ultralight.UltralightPlatform;
import com.labymedia.ultralight.UltralightRenderer;
import com.labymedia.ultralight.config.FontHinting;
import com.labymedia.ultralight.config.UltralightConfig;
import com.labymedia.ultralight.gpu.UltralightGPUDriverNativeUtil;

import java.io.IOException;
import java.nio.file.Path;

public class UltraLight {
    private static UltralightRenderer renderer;

    private UltraLight() {}
    public static void init() {
        try {
            Path path = Gdx.files.internal("ultralight/bin").file().toPath().toAbsolutePath();
            System.out.println("loading from here! " + path);
            UltralightJava.extractNativeLibrary(path);
            UltralightGPUDriverNativeUtil.extractNativeLibrary(path);

            UltralightJava.load(path);
            UltralightGPUDriverNativeUtil.load(path);
        } catch (UltralightLoadException | IOException e) {
            throw new RuntimeException(e);
        }

        UltralightPlatform platform = UltralightPlatform.instance();
        platform.setConfig(
                new UltralightConfig()
                        .resourcePath(Gdx.files.internal("ultralight/resources").file().toPath().toAbsolutePath().toString())
                        .fontHinting(FontHinting.SMOOTH)
        );
        platform.usePlatformFontLoader();
        platform.usePlatformFileSystem(Gdx.files.internal("/").path());
        renderer = UltralightRenderer.create();

        System.out.println("Ultralight Loaded Successfully");
    }

    public static UltralightRenderer getRenderer() {
        return renderer;
    }

}
