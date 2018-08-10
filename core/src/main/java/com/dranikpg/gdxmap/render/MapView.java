package com.dranikpg.gdxmap.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.dranikpg.gdxmap.GdxMapCodes;
import com.dranikpg.gdxmap.MapHolder;
import com.dranikpg.gdxmap.opt.POT;
import com.dranikpg.gdxmap.abstr.TileRenderer;

public class MapView implements TileRenderer, Disposable {

    MapHolder hd;

    OrthographicCamera c;
    Batch b;
    boolean ownsbatch;

    int w,h;
    FrameBuffer bf;

    protected Vector3 target = new Vector3();
    protected boolean reached = true;

    public float zoomalpha = 0.2f;
    public float movelapha = 0.1f;
    public float distol = 10;
    public float zoomjump = 0.1f;
    public int transitionFetch = GdxMapCodes.FETCH_FILEIO;

    public MapView(MapHolder hd, Batch b, int w, int h){
        this(hd,b,w,h,false);
    }

    public MapView(MapHolder hd, int w, int h){
        this(hd, new SpriteBatch(100), w,h,true);
    }

    private MapView(MapHolder hd, Batch b, int w, int h, boolean owns){
        this.hd = hd;
        this.b = b;
        this.w = w;
        this.h = h;
        ownsbatch = owns;

        c = new OrthographicCamera(w,h);
        c.setToOrtho(false,w,h);
        hd.setProjection(c);

        hd.renderer(this);

        resize(w,h);
    }

    public void update(){

        if(!reached) hd.update(transitionFetch);
        else hd.update(GdxMapCodes.FETCH_INTERNET);

        if(!reached){

            int level = (int) target.z;
            target.z = 0;

            c.position.lerp(target,movelapha);

            float dist = c.position.dst(target);

            if(level > hd.level()){
                if(dist < distol*(hd.maxlevel()-hd.level())) {
                    if (level > hd.level()) zoom(-zoomalpha);
                    else if(level < hd.level()) zoom(zoomalpha);
                }
            }else if(level < hd.level()){
                if (level > hd.level()) zoom(-zoomalpha);
                else if(level < hd.level()) zoom(zoomalpha);
            }

            if( dist < 2f && hd.level() == level){
                reached = true;
            }

            target.z = level;
            c.update();
            hd.forceRedraw();
        }

    }

    /*
    Pos & zoom
     */

    protected void fixzoom(){
        float zoomdif = Math.min(2,1+zoomjump*3);
        if(c.zoom > 1f+zoomjump && hd.level() > 0){
            hd.level(hd.level()-1);
            levelchange(-1);
            c.zoom/=zoomdif;
        }else if(c.zoom < 1-zoomjump && hd.level() < hd.maxlevel()){
            hd.level(hd.level()+1);
            levelchange(+1);
            c.zoom*=zoomdif;
        }
    }

    protected void levelchange(int dif) {
        float  sc = POT.of(dif);
        target.x *= sc;
        target.y *= sc;
    }

    public void zoom(float dif){
        c.zoom+=dif;
        fixzoom();
    }

    public void move(float dx, float dy){
        c.translate(dx,dy);
        c.update();
    }

    public void goal(float x, float y, int level){
        float sc = POT.of(hd.level()-level);
        target.setZero();
        target.z = level;
        target.x = (x*1f*sc)*hd.getTileSize();
        target.y = (y*1f*sc)*hd.getTileSize();
        reached = false;
    }

    /*
     util
     */


    public Texture resize(int w, int h){
        if(bf != null) bf.dispose();
        bf = new FrameBuffer(Pixmap.Format.RGBA8888,w,h,false,false);

        c.viewportWidth = w;
        c.viewportHeight = h;
        c.update();

        hd.forceRedraw();

        return bf.getColorBufferTexture();
    }

    public Texture texture(){
        return bf.getColorBufferTexture();
    }

    public OrthographicCamera camera(){
        return c;
    }

    @Override
    public int sizehint(int a) {
        if(!reached)return hd.maxlevel();
        else return 5;
    }

    @Override
    public void begin(OrthographicCamera cam) {
        bf.begin();
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        b.setProjectionMatrix(cam.combined);
        b.begin();
    }

    @Override
    public void render(TextureRegion t, float x, float y, int ts) {
        b.draw(t,x,y,ts,ts);
    }


    public void renderEnclave(Batch batch, OrthographicCamera cam, float w, float h){

    }

    @Override
    public void end() {
        renderEnclave(b,c, c.viewportWidth, c.viewportHeight);
        b.end();
        bf.end();
    }


    @Override
    public void dispose() {
        b.dispose();
        bf.dispose();
    }
}
