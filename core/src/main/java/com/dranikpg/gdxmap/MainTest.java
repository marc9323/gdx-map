package com.dranikpg.gdxmap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dranikpg.gdxmap.abstr.MapExecutionProvider;
import com.dranikpg.gdxmap.impl.CachedEncodedTileProvider;
import com.dranikpg.gdxmap.provider.OSMProviderInfo;
import com.dranikpg.gdxmap.render.MapView;
import com.dranikpg.gdxmap.strategy.InstantWriteBytePersistence;
import com.dranikpg.gdxmap.strategy.LimitedBoundsEjectMemoryPersistence;
import com.dranikpg.gdxmap.strategy.LimitedBoundsEjectNetStrategy;

import java.util.Arrays;

public class MainTest extends ApplicationAdapter implements InputProcessor, MapExecutionProvider {

    SpriteBatch b;

    MapHolder hd;
    MapView rd;

    ScreenViewport vp;
    TextureRegion r;

    @Override
    public void create() {
        // Generate bit masks for bitwise operations
        GdxMapCodes.generate();
        /*
            Optionally set max bounds BEFORE generating
            GdxMapCodes.MAX_LEVEL
            GdxMapCodes.MAX_COORD
         */

        //SpriteBatch for rendering
        b = new SpriteBatch();

        vp = new ScreenViewport(new OrthographicCamera());

        //create out MapHolder
        /*
            MapHolder::update expects the maximum fetch type, which can be found in GdxMapCodes.
            Look into MapView for an example
         */
        hd = new MapHolder();


        /* Main tile provider
           CachedEncodedTileProvider is a robust implementation ready to use
           LimitedBoundsEjectNetStrategy ejects tiles that are not visible any more and restricts the amount of download threads
           LimitedBoundsEjectMemoryPersistence auto disposal when reaching given size
           InstantWriteBytePersistence writes everything it gets, you might want to eject some tiles so the folder wont blow up the fs
           OSMProviderInfo provider info based on OpenStreetMap
        */
        CachedEncodedTileProvider pv = new CachedEncodedTileProvider(
                this,
                new LimitedBoundsEjectNetStrategy(3),
                new LimitedBoundsEjectMemoryPersistence(500),
                new InstantWriteBytePersistence(Gdx.files.external("tmp/gdxmap")),
                new OSMProviderInfo()
        );

        hd.provider(pv);
        /* MapView keeps an internal framebuffer
            and managers rendering
        */
        rd = new MapView(hd, b, 500, 500);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        rd.update();

        vp.apply();

        b.setProjectionMatrix(vp.getCamera().combined);
        b.begin();
        b.draw(r,0,0,vp.getScreenWidth(),vp.getScreenHeight());
        b.end();
    }

    @Override
    public void resize(int width, int height) {
        r = new TextureRegion(rd.resize(width, height));
        r.flip(false,true);
        vp.update(width, height,true);
    }

    @Override
    public void ioRun(Runnable r) {
        new Thread(r).start();
    }

    @Override
    public void genericRun(Runnable r) {
        new Thread(r).start();
    }

    @Override
    public void openGLRun(Runnable r) {
        Gdx.app.postRunnable(r);
    }


    @Override
    public boolean keyDown(int keycode) {
        OrthographicCamera cam = rd.camera();
        switch (keycode){
            case Input.Keys.W:rd.move(0,100);
                break;
            case Input.Keys.S:rd.move(0,-100);
                break;
            case Input.Keys.A:rd.move(-100,0);
                break;
            case Input.Keys.D:rd.move(100,0);
                break;
            case Input.Keys.E:rd.zoom(0.1f);
                break;
            case Input.Keys.R:rd.zoom(-0.1f);
                break;
            case Input.Keys.BACKSPACE:
                Vector2 v = new Vector2();
                GeoUtil.getCoords(v,7d,3d, 10, hd.getGridSizeForLevel(10));
                rd.goal(v.x,v.y,10);
                break;
            case Input.Keys.ENTER:
                v = new Vector2();
                double[] ar = hd.getGEO();
                System.out.print(Arrays.toString(ar));
                break;
        }
        cam.update();
        hd.forceRedraw();
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}