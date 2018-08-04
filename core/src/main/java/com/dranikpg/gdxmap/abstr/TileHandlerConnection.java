package com.dranikpg.gdxmap.abstr;

import com.dranikpg.gdxmap.MapHolder;

import java.util.List;


public interface TileHandlerConnection extends LoadListener {

    MapExecutionProvider THgetExecution();

    List<MapHolder> THgetHolders();

    TileProviderInfo THgetProviderInfo();

}
