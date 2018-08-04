package com.dranikpg.gdxmap.impl;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.LongMap;
import com.dranikpg.gdxmap.GdxMapCodes;
import com.dranikpg.gdxmap.MapHolder;
import com.dranikpg.gdxmap.opt.POT;
import com.dranikpg.gdxmap.abstr.*;
import com.dranikpg.gdxmap.macro.MapTile;

import java.util.ArrayList;
import java.util.List;

/**
 * Double Cached
 * Downloading
 * Long encoding
 * Tile provider
 */
public class CachedEncodedTileProvider implements TileProvider, TileHandlerConnection, Disposable {

    LongMap<MapTile> tm;

    MapExecutionProvider exec;
    List<MapHolder> hd;

    MemoryPersistenceStrategy memS;
    PersistenceStrategy fileS;

    InternetStrategy ihan;

    TileProviderInfo info;
    int tilesize;

    public CachedEncodedTileProvider(MapExecutionProvider exec, InternetStrategy ihan, MemoryPersistenceStrategy memS, PersistenceStrategy fileS, TileProviderInfo info) {
        this.exec = exec;
        this.ihan = ihan;
        this.hd = new ArrayList<MapHolder>(1);
        this.memS = memS;
        this.fileS = fileS;
        this.info = info;
        tm = new LongMap<MapTile>();
        tilesize = info.tileSize();
    }


    @Override
    public int tilesize() {
        return tilesize;
    }

    @Override
    public int gridsize(int level) {
        return info.gridsize(level);
    }

    @Override
    public int maxlevel() {
        return info.maxlevel();
    }

    @Override
    public void register(MapHolder hd) {
        this.hd.add(hd);
    }

    @Override
    public void remove(MapHolder _hd) {
        hd.remove(_hd);
    }

    @Override
    public boolean valid(int level, int x, int y) {
        return info.valid(x,y,level);
    }

    @Override
    public MapTile get(long code) {
        MapTile t = tm.get(code);
        if(t == null){
            if(!valid(GdxMapCodes.level(code), GdxMapCodes.x(code), GdxMapCodes.y(code)))
                return null;
            t = new MapTile(code);
            insert(t);
        }
        return t;
    }

    @Override
    public MapTile get(int level, int x, int y) {
        if(!valid(level, x, y))return null;
        return get(GdxMapCodes.code(level, x, y));
    }

    public void insert(MapTile t){
        tm.put(t.code, t);
    }

    /* load */

    @Override
    public void load(long code, int load, int loadp) {
        load(get(code), load, loadp);
    }

    @Override
    public void load(int level, int x, int y, int load, int loadp) {
        load(get(level, x, y), load, load);
    }


    protected void load(MapTile t, int load, int loadp){
        if(!t.needsload()) return;
        t.setLoading(loadp);
        if(load >= GdxMapCodes.FETCH_FILEIO && fileS.has(t.code)){
            readFromCache(t);
        }else if(load == GdxMapCodes.FETCH_INTERNET){
            fetchFromInternet(t);
        }
    }

    protected void fetchFromInternet(final MapTile t) {
        ihan.request(t,this);
    }

    protected void readFromCache(MapTile t){
        fileS.read(t, this);
    }

    @Override
    public void loaded(byte[] ar, MapTile t, FetchType type) {
        if(type != FetchType.STORAGE) fileS.fetched(ar, t.code, this);
        memS.fetched(t, this);
        for(MapHolder h : hd)h.loaded(t);
    }

    /* */

    TextureRegion r = new TextureRegion();
    @Override
    public TextureRegion find(int level, int x, int y, int depth, int load)
    {
        MapTile t = get(level, x, y);
        if(t != null && t.isLoaded()){
            r.setRegion(t.texture());
            return r;
        }else{
            if(t != null && load!=GdxMapCodes.FETCH_NONE && !t.isLoading())load(t, load, GdxMapCodes.VIEW_LOAD);
            if(depth > 0)
                return find(level-1,x/2,y/2,x%2,y%2,1,depth-1);
            else return null;
        }
    }

    @Override
    public TextureRegion find(int level, int x, int y, int xp, int yp, int steps, int depth){
        if(level < 1)return null;
        MapTile t = get(level, x, y);

        int div = (int) POT.of(steps);
        if(t != null && t.isLoaded()){
            r.setTexture(t.texture());
            r.setRegion(1f/div*xp,1f/div*yp,1f/div*(xp+1),1f/div*(yp+1));
            return r;
        }else if(depth > 0){
            int xp2 = (x%2==0?0:1)*div+xp;
            int yp2 = (y%2==0?0:1)*div+yp;
            return find(level-1,x/2,y/2,xp2,yp2,steps+1,depth-1);
        }

        return null;
    }


    @Override
    public MapExecutionProvider THgetExecution() {
        return exec;
    }

    @Override
    public List<MapHolder> THgetHolders() {
        return hd;
    }

    @Override
    public TileProviderInfo THgetProviderInfo() {
        return info;
    }

    @Override
    public LongMap<MapTile> THgetTileMap() {
        return tm;
    }

    @Override
    public void dispose() {
        for(MapTile t: tm.values())t.dispose();
    }
}
