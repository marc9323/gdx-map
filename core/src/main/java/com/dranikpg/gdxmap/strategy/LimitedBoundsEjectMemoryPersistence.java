package com.dranikpg.gdxmap.strategy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import com.dranikpg.gdxmap.MapHolder;
import com.dranikpg.gdxmap.abstr.MemoryPersistenceStrategy;
import com.dranikpg.gdxmap.abstr.TileHandlerConnection;
import com.dranikpg.gdxmap.macro.MapTile;

import java.util.List;

public class LimitedBoundsEjectMemoryPersistence implements MemoryPersistenceStrategy {

    int limit;
    public int levelT = 4;
    public int distanceT = 10;
    boolean remove = true;

    Array<Texture> tar = new Array<Texture>();

    volatile boolean running = false;

    public LimitedBoundsEjectMemoryPersistence(int limit, int levelT, int distanceT, boolean remove) {
        this.limit = limit;
        this.levelT = levelT;
        this.distanceT = distanceT;
        this.remove = remove;
    }

    public LimitedBoundsEjectMemoryPersistence(int limit) {
        this.limit = limit;
    }

    private void runeject(final TileHandlerConnection cn){
        LongMap<MapTile> m = cn.THgetTileMap();
        List<MapHolder> hds = cn.THgetHolders();

        for(MapTile t: m.values()){

            if(t == null || !t.isLoaded())continue;

            boolean keep = false;
            for(MapHolder hd: hds){
                if(hd.inBoundsWithTolerance(t.x,t.y,t.level,distanceT,levelT)) {
                    keep = true;
                    break;
                }
            }
            if(!keep){
                if(remove)m.remove(t.code);
                tar.add(t.texture());
                t = null;
            }
        }

        cn.THgetExecution().openGLRun(
                new Runnable() {
                    @Override
                    public void run() {
                        for(Texture t: tar)t.dispose();
                        tar.clear();
                        running = false;
                        System.out.println("end: " + cn.THgetTileMap().size);
                    }
                }
        );

    }

    @Override
    public void fetched(MapTile t, final TileHandlerConnection cn) {
        if(!running && cn.THgetTileMap().size > limit) {
            System.out.println(cn.THgetTileMap().size);
            running = true;
            cn.THgetExecution().genericRun(
                    new Runnable() {
                        @Override
                        public void run() {
                            runeject(cn);
                        }
                    }
            );
        }
    }
}
