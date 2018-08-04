package com.dranikpg.gdxmap.abstr;

import com.badlogic.gdx.utils.LongMap;
import com.dranikpg.gdxmap.MapHolder;
import com.dranikpg.gdxmap.macro.MapTile;

import java.util.List;


public interface TileHandlerConnection extends LoadListener {

    MapExecutionProvider THgetExecution();

    List<MapHolder> THgetHolders();

    TileProviderInfo THgetProviderInfo();

    LongMap<MapTile> THgetTileMap();

}
