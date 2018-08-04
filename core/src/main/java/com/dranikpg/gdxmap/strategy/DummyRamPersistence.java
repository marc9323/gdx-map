package com.dranikpg.gdxmap.strategy;

import com.dranikpg.gdxmap.abstr.MemoryPersistenceStrategy;
import com.dranikpg.gdxmap.abstr.TileHandlerConnection;
import com.dranikpg.gdxmap.macro.MapTile;

public class DummyRamPersistence implements MemoryPersistenceStrategy {

    @Override
    public void fetched(MapTile t, TileHandlerConnection cn) {

    }
}
