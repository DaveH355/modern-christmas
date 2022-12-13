package com.dave.modernchristmas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetFinder {
    private final AssetManagerResolving assetManager;
    private static final List<String> folderBlacklist = new ArrayList<>();

    private static final Map<String, Class<?>> extensionWhitelist = new HashMap<>();

    static {
        extensionWhitelist.put("mp3", Sound.class);
        extensionWhitelist.put("ogg", Sound.class);
        extensionWhitelist.put("wav", Sound.class);
        extensionWhitelist.put("png", Texture.class);
        extensionWhitelist.put("fnt", BitmapFont.class);
        extensionWhitelist.put("atlas", TextureAtlas.class);
        extensionWhitelist.put("tmx", TiledMap.class);

        folderBlacklist.add("ultralight/");
        folderBlacklist.add("skin/");

    }
    public AssetFinder(AssetManagerResolving assetManager) {
        this.assetManager = assetManager;
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());
    }
    public void load() {
        Array<String> allFiles = new Array<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.internal("assets.txt").read()));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                allFiles.add(line);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        allFiles.forEach(string -> {
            FileHandle fileHandle = Gdx.files.internal(string);

            if (folderBlacklist.stream().anyMatch(blacklist -> fileHandle.path().startsWith(blacklist))) {
                return;
            }

            if (extensionWhitelist.containsKey(fileHandle.extension())) {
                assetManager.load(fileHandle.path(), extensionWhitelist.get(fileHandle.extension()));
            }
        });
        System.out.println(allFiles.size + " possible files found");
    }



}