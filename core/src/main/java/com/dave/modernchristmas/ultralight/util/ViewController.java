/*
 * Ultralight Java - Java wrapper for the Ultralight web engine
 * Copyright (C) 2020 - 2021 LabyMedia and contributors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.dave.modernchristmas.ultralight.util;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.dave.modernchristmas.ultralight.js.JavaScriptBridge;
import com.dave.modernchristmas.ultralight.listener.LoadListener;
import com.labymedia.ultralight.UltralightRenderer;
import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.bitmap.UltralightBitmap;
import com.labymedia.ultralight.bitmap.UltralightBitmapSurface;
import com.labymedia.ultralight.input.UltralightMouseEvent;
import com.labymedia.ultralight.input.UltralightMouseEventButton;
import com.labymedia.ultralight.input.UltralightMouseEventType;
import com.labymedia.ultralight.javascript.JavascriptContextLock;

import java.nio.ByteBuffer;


/**
 * Class used for controlling the WebGUI rendered on top of the OpenGL GUI.
 */
public class ViewController {
    private final UltralightRenderer renderer;
    private final UltralightView view;

    private final LoadListener loadListener;
    private final JavaScriptBridge bridge;
    private Pixmap pixmap;
    private Texture texture;

    private long lastJavascriptGarbageCollections;

    /**
     * Constructs a new {@link ViewController} and retrieves the platform.
     */
    public ViewController(UltralightRenderer renderer, UltralightView view) {
        this.renderer = renderer;


        this.view = view;
        this.bridge = new JavaScriptBridge(view);
        this.loadListener = new LoadListener(view);
        this.view.setLoadListener(loadListener);

        pixmap = new Pixmap((int) view.width(), (int) view.height(), Pixmap.Format.RGBA8888);


        this.lastJavascriptGarbageCollections = 0;
    }


    /**
     * Loads the specified URL into this controller.
     *
     * @param url The URL to load
     */
    public void loadURL(String url) {
        this.view.loadURL(url);
    }

    public JavaScriptBridge getJSBridge() {
        return bridge;
    }

    /**
     * Updates and renders the renderer
     */
    public void update() {
        this.renderer.update();
        this.renderer.render();

        if(lastJavascriptGarbageCollections == 0) {
            lastJavascriptGarbageCollections = System.currentTimeMillis();
        } else if(System.currentTimeMillis() - lastJavascriptGarbageCollections > 1000) {
            System.out.println("Garbage collecting Javascript...");
            try(JavascriptContextLock lock = this.view.lockJavascriptContext()) {
                lock.getContext().garbageCollect();
            }
            lastJavascriptGarbageCollections = System.currentTimeMillis();
        }
    }

    /**
     * Resizes the web view.
     *
     * @param width  The new view width
     * @param height The new view height
     */
    public void resize(int width, int height) {
        this.view.resize(width, height);
    }


    public void render(Batch batch) {
        UltralightBitmapSurface surface = (UltralightBitmapSurface) this.view.surface();
        UltralightBitmap bitmap = surface.bitmap();


        if (texture != null) texture.dispose();
        ByteBuffer byteBuffer = bitmap.lockPixels();

        pixmap = new Pixmap((int) view.width(), (int) view.height(), Pixmap.Format.RGBA8888);
        pixmap.setPixels(byteBuffer);
        texture = new Texture(pixmap);
        batch.draw(texture, 0, 0);



        pixmap.dispose();
        bitmap.unlockPixels();


    }

    public UltralightView getView() {
        return view;
    }


    public void onMouseDown(int x, int y, int button) {

        UltralightMouseEvent event = onMouse(x, y, button);
        event.type(UltralightMouseEventType.DOWN);
        view.fireMouseEvent(event);
    }
    public void onMouseUp(int x, int y, int button) {
        UltralightMouseEvent event = onMouse(x, y, button);
        event.type(UltralightMouseEventType.UP);
        view.fireMouseEvent(event);
    }
    private UltralightMouseEvent onMouse(int x, int y, int button) {
        UltralightMouseEvent event = new UltralightMouseEvent();
        UltralightMouseEventButton ultralightButton = UltralightMouseEventButton.MIDDLE;
        if (button == Input.Buttons.LEFT) {
            ultralightButton = UltralightMouseEventButton.LEFT;
        } else if (button == Input.Buttons.RIGHT) {
            ultralightButton = UltralightMouseEventButton.RIGHT;
        }
        event.button(ultralightButton);
        event.x(x);
        event.y(y);
        return event;
    }

    public void onMouseMove(int x, int y) {
        UltralightMouseEvent event = new UltralightMouseEvent();
        event.x(x);
        event.y(y);
        event.type(UltralightMouseEventType.MOVED);
        view.fireMouseEvent(event);
    }



}
