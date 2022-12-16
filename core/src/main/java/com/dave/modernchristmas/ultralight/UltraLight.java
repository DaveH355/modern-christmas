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
import java.nio.file.*;

public class UltraLight {
    private static UltralightRenderer renderer;

    private UltraLight() {}
    public static void init() {
        try {
            load(Gdx.files.internal("ultralight/bin").file().toPath());
        } catch (UltralightLoadException | IOException e) {
           try {
               load(Gdx.files.local("ultralight/bin").file().toPath());
           } catch (UltralightLoadException | IOException e2) {
               throw new RuntimeException(e2);
           }
        }

        UltralightPlatform platform = UltralightPlatform.instance();
        platform.setConfig(
                new UltralightConfig()
                        .resourcePath(Gdx.files.local("ultralight/resources").path())
                        .fontHinting(FontHinting.SMOOTH)
        );
        platform.usePlatformFontLoader();
        platform.usePlatformFileSystem(Gdx.files.local("ultralight/").path());

        renderer = UltralightRenderer.create();


        System.out.println("Ultralight Loaded Successfully");
    }

    private static void load(Path path) throws UltralightLoadException, IOException {
        UltralightJava.extractNativeLibrary(path);
        UltralightGPUDriverNativeUtil.extractNativeLibrary(path);

        UltralightJava.load(path);
        UltralightGPUDriverNativeUtil.load(path);
    }

    public static UltralightRenderer getRenderer() {
        return renderer;
    }

}
