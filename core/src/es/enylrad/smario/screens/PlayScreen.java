package es.enylrad.smario.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import es.enylrad.smario.MarioBrossMain;
import es.enylrad.smario.scenes.Hud;
import es.enylrad.smario.sprites.Mario;

public class PlayScreen implements Screen {
    private MarioBrossMain game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Mario player;

    public PlayScreen(MarioBrossMain game) {
        this.game = game;

        gamecam = new OrthographicCamera();

        gamePort = new FitViewport(MarioBrossMain.V_WIDTH / MarioBrossMain.PPM, MarioBrossMain.V_HEIGHT / MarioBrossMain.PPM, gamecam);

        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBrossMain.PPM);

        gamecam.position.set(gamePort.getScreenWidth() / 2, gamePort.getScreenHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
        this.player = new Mario(world);

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body = null;

        //Create ground bodies/fixtures
        createBodiesFixtures(bdef, shape, fdef, body, 2);

        //Create pipe bodies/fixtures
        createBodiesFixtures(bdef, shape, fdef, body, 3);
        //Create brick bodies/fixtures
        createBodiesFixtures(bdef, shape, fdef, body, 5);
        //Create coin bodies/fixtures
        createBodiesFixtures(bdef, shape, fdef, body, 4);
    }

    public void createBodiesFixtures(BodyDef bdef, PolygonShape shape, FixtureDef fdef, Body body, int layer) {
        for (MapObject object : map.getLayers().get(layer).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBrossMain.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBrossMain.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBrossMain.PPM, rect.getHeight() / 2 / MarioBrossMain.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2){
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2){
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }

    }

    public void update(float dt) {
        handleInput(dt);

        world.step(1 / 60f, 6, 2);

        gamecam.position.x = player.b2body.getPosition().x;

        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
