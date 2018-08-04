package com.dranikpg.gdxmap.strategy;

import com.dranikpg.gdxmap.abstr.PersistenceStrategy;
import com.dranikpg.gdxmap.abstr.TileHandlerConnection;
import com.dranikpg.gdxmap.macro.MapTile;

public class DummyPersistance implements PersistenceStrategy {
    @Override
    public void read(MapTile t, TileHandlerConnection cn) {

    }

    @Override
    public boolean has(long code) {
        return false;
    }

    @Override
    public void fetched(byte[] ar, long code, TileHandlerConnection cn) {

    }
}
