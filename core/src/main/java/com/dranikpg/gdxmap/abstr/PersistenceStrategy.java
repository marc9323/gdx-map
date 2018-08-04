package com.dranikpg.gdxmap.abstr;

import com.dranikpg.gdxmap.macro.MapTile;

public interface PersistenceStrategy {

    void read(MapTile t, TileHandlerConnection cn);

    boolean has(long code);

    void fetched(byte[] ar, long code, TileHandlerConnection cn);

}
