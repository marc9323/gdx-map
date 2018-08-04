package com.dranikpg.gdxmap.abstr;

import com.dranikpg.gdxmap.macro.MapTile;

public interface InternetStrategy {

    void request(MapTile t,TileHandlerConnection cn);

}
