package com.dranikpg.gdxmap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dranikpg.gdxmap.abstr.TileProvider;
import com.dranikpg.gdxmap.abstr.TileRenderer;
import com.dranikpg.gdxmap.macro.MapTile;
import com.dranikpg.gdxmap.opt.POT;

public class MapHolder {

    OrthographicCamera cam;
    TileProvider pv;
    TileRenderer rd;

    volatile boolean redraw = true;
    int level = 1;

    float vpfactor = 1.5f;


    public void provider(TileProvider pv) {
        this.pv = pv;
        pv.register(this);
    }

    public void renderer(TileRenderer rd) {
        this.rd = rd;
    }

    public void setProjection(OrthographicCamera cam){
        this.cam = cam;
    }

    public void forceRedraw(){
        redraw = true;
    }

    public void update(int load){
        if(redraw) redraw(load);
    }

    private void redraw(int load) {
        //System.out.println("Redraw " + load);
        int ts = pv.tilesize();
        int w = MathUtils.ceil(cam.viewportWidth*vpfactor*cam.zoom/ts);
        int h = MathUtils.ceil(cam.viewportHeight*vpfactor*cam.zoom/ts);
        int xs = MathUtils.floor (cam.position.x/ts-w/2);
        int ys = MathUtils.floor (cam.position.y/ts-h/2);
        int xe = xs+w;
        int ye = ys+h;
        int gs = pv.gridsize(level);

        int depth = rd.sizehint(w*h);
        rd.begin(cam);

        TextureRegion r;
        for(int x = xs; x <= xe; x++ ){
            for(int y = ys; y <= ye; y++){
                int xt = x;
                int yt = (gs-1-y);

                if(xt < 0)xt = gs-Math.abs(xt);
                if(xt >= gs) xt = xt%(gs);

                r = pv.find(level,xt,yt,depth,load);
                if(r != null) rd.render(r,x*ts,y*ts,ts);
            }
        }
        rd.end();
        redraw = false;
    }

    public int level(){
        return level;
    }

    public void level(int l){
        int d = l-level;
        float sc = POT.of(d);
        cam.position.scl(sc);
        level = l;
        forceRedraw();
    }



    public void loaded(MapTile t){
        if(inBoundsWithTolerance(t.x,t.y,t.level,0,0))
                forceRedraw();
    }

    public int getTileSize(){
        return pv.tilesize();
    }

    public int getGridSize(){
        return pv.gridsize(level);
    }

    public int getGridSizeForLevel(int level){
        return pv.gridsize(level);
    }

    public void unregister(){
        pv.remove(this);
    }

    /*
        Position access
     */
    public boolean inBoundsWithTolerance(float x, float y, int gl,int dt,  int lt){
        if(Math.abs(level-gl) > lt)return false;
        float sc =  POT.of(level-gl);
        x = x*sc;
        y = y*sc;
        y = pv.gridsize(level) - y;
        int ts = pv.tilesize();
        int w = MathUtils.ceil(cam.viewportWidth*vpfactor*cam.zoom/ts);
        int h = MathUtils.ceil(cam.viewportHeight*vpfactor*cam.zoom/ts);
        int xs = MathUtils.floor (cam.position.x/ts-w/2);
        int ys = MathUtils.floor (cam.position.y/ts-h/2);
        int xe = xs+w;
        int ye = ys+h;
        return xs-dt <= x && x <= xe+dt && ys-dt <= y && y <= ye+dt;
    }

    public void getTilePos(Vector2 t){
        t.x = (int) cam.position.x/pv.tilesize();
        t.y = (int) cam.position.y/pv.tilesize();
        t.y = pv.gridsize(level) - t.y;
    }

    public void getTilePoswBase(Vector2 t, int base){
        float sc =  POT.of(base-level)/cam.zoom;
        t.x*=sc;
        t.y*=sc;
        t.x = cam.position.x/pv.tilesize();
        t.y = cam.position.y/pv.tilesize() ;
    }

    /*
        WITH FLOATS
     */
    public void getPixelPos(Vector2 t, int z){
        float sc = POT.of(level-z);
        t.x *=pv.tilesize() * sc;
        t.y *=pv.tilesize() * sc;
    }

    public void project(Vector2 v, Vector3 calc){
        calc.set(v,0);
        cam.project(calc,
               0,0,cam.viewportWidth,cam.viewportHeight);

        v.x = calc.x;
        v.y = calc.y;
    }


    public double[] getGEO(){
        double[] d = new double[2];
        float xt =  (cam.position.x / pv.tilesize());
        float yt =  (cam.position.y / pv.tilesize());
        yt = pv.gridsize(level) - yt;
        GeoUtil.getLatLon(d,xt, yt,level);
        return d;
    }


    public int maxlevel() {
        return pv.maxlevel();
    }
}
