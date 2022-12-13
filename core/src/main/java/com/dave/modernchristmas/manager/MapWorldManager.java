package com.dave.modernchristmas.manager;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dave.modernchristmas.AssetManagerResolving;
import com.dave.modernchristmas.Constants;
import com.dave.modernchristmas.GameData;
import com.dave.modernchristmas.TiledMapRendererBleeding;
import jdk.vm.ci.meta.Constant;

public class MapWorldManager {
    private World world;
    private TiledMapRenderer renderer;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private static final float TIME_STEP = 1/240f;
    private float accumulator;

    public MapWorldManager(OrthographicCamera camera) {
        this.camera = camera;

        AssetManagerResolving assetManager = GameData.getInstance().getGame().getAssetManager();
        TiledMap tiledMap = assetManager.get("untitled.tmx", TiledMap.class);

        world = new World(new Vector2(0, -30), true);
        renderer = new TiledMapRendererBleeding(tiledMap, 1/ Constants.PIXELS_PER_METER);
        debugRenderer = new Box2DDebugRenderer();

        loadMap(tiledMap);

    }

    public void update(float delta) {
        //update box2d world
        float frameTime = Math.min(delta, 0.25f);
        accumulator += frameTime;
        while (accumulator > TIME_STEP) {
            world.step(TIME_STEP, 6, 3);
            accumulator -= TIME_STEP;
        }

        debugRenderer.render(world, camera.combined);

        //render map
        renderer.setView(camera);
        renderer.render();

    }

    private void loadMap(TiledMap map) {
        for (MapLayer mapLayer : map.getLayers()) {
            for (MapObject object : mapLayer.getObjects()) {
                Shape shape;
                if (object instanceof RectangleMapObject) {
                    shape = getRectangle((RectangleMapObject)object);
                }
                else if (object instanceof PolygonMapObject) {
                    shape = getPolygon((PolygonMapObject)object);
                }
                else if (object instanceof PolylineMapObject) {
                    shape = getPolyline((PolylineMapObject)object);
                }
                else if (object instanceof CircleMapObject) {
                    shape = getCircle((CircleMapObject)object);
                }
                else {
                    continue;
                }
                BodyDef bdef = new BodyDef();
                FixtureDef fdef = new FixtureDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                Body body = world.createBody(bdef);
                fdef.shape = shape;
                body.createFixture(fdef);
            }

        }
    }

    public World getWorld() {
        return world;
    }

    // https://stackoverflow.com/questions/45805732/libgdx-tiled-map-box2d-collision-with-polygon-map-object
    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / Constants.PIXELS_PER_METER,
                (rectangle.y + rectangle.height * 0.5f ) / Constants.PIXELS_PER_METER);
        polygon.setAsBox(rectangle.width * 0.5f /Constants.PIXELS_PER_METER,
                rectangle.height * 0.5f / Constants.PIXELS_PER_METER,
                size,
                0.0f);
        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / Constants.PIXELS_PER_METER);
        circleShape.setPosition(new Vector2(circle.x / Constants.PIXELS_PER_METER, circle.y / Constants.PIXELS_PER_METER));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / Constants.PIXELS_PER_METER;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / Constants.PIXELS_PER_METER;
            worldVertices[i].y = vertices[i * 2 + 1] / Constants.PIXELS_PER_METER;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}
