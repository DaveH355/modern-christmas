package com.dave.modernchristmas.ultralight;

import com.badlogic.gdx.Gdx;
import com.labymedia.ultralight.UltralightJava;
import com.labymedia.ultralight.UltralightLoadException;
import com.labymedia.ultralight.UltralightPlatform;
import com.labymedia.ultralight.UltralightRenderer;
import com.labymedia.ultralight.config.FontHinting;
import com.labymedia.ultralight.config.UltralightConfig;
import com.labymedia.ultralight.gpu.UltralightGPUDriverNativeUtil;
import com.labymedia.ultralight.os.Architecture;
import com.labymedia.ultralight.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

public class UltraLight {
    private static UltralightRenderer renderer;

    private UltraLight() {}
    public static void init() {
        try {
            Path loadPath = Gdx.files.internal("ultralight/bin").file().toPath().toAbsolutePath();
            UltralightJava.extractNativeLibrary(loadPath);
            UltralightGPUDriverNativeUtil.extractNativeLibrary(loadPath);

            UltralightJava.load(loadPath);
            UltralightGPUDriverNativeUtil.load(loadPath);
        } catch (UltralightLoadException | IOException e) {
            throw new RuntimeException(e);
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

    public static void copyFolder(Path source, Path target, CopyOption... options)
            throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir).toString()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.copy(file, target.resolve(source.relativize(file).toString()), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }


    public static UltralightRenderer getRenderer() {
        return renderer;
    }

}
