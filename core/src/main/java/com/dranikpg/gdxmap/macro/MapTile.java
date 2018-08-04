package com.dranikpg.gdxmap.macro;

import com.badlogic.gdx.graphics.Texture;
import com.dranikpg.gdxmap.GdxMapCodes;

public class MapTile {

    public int level;
    public int x, y;
    public long code;

    public int loadp = -1;
    public boolean holdInCache;

    public MapTile(long code) {
        this.level = GdxMapCodes.level(code);
        this.x = GdxMapCodes.x(code);
        this.y = GdxMapCodes.y(code);
        this.code = code;
    }

    public MapTile(int level, int x, int y) {
        this.level = level;
        this.x = x;
        this.y = y;
        this.code = GdxMapCodes.code(this);
    }

    // just so to be safe
    private volatile boolean loading = false;
    private Texture t = null;


    public void provideTexture(Texture t){
        if(this.t != null) dispose();
        this.t = t;
        loading = false;
    }

    public Texture texture(){
        return t;
    }

    public void setLoading(int p){
        loadp = p;
        loading = true;
    }

    public void cancelLoad(){
        loading = false;
       // loadp = -1;
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isLoaded(){
        return t != null;
    }

    public boolean needsload(){
        return !isLoaded() && !isLoading();
    }

    public void dispose(){
        if(t != null)t.dispose();
        t = null;
    }

    @Override
    public String toString() {
        return "MapTile{" +
                "level=" + level +
                ", x=" + x +
                ", y=" + y +
                ", loading=" + loading +
                ", loaded=" + isLoaded() +
                '}';
    }
}
