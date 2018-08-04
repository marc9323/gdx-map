package com.dranikpg.gdxmap.abstr;

import com.dranikpg.gdxmap.macro.MapTile;

public interface MemoryPersistenceStrategy {

    void fetched(MapTile t, TileHandlerConnection cn);

}
