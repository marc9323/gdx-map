package com.dranikpg.gdxmap.abstr;

import com.dranikpg.gdxmap.macro.MapTile;

public interface TileProviderInfo {

    int tileSize();

    int gridsize(int level);

    int maxlevel();

    String getUrl(MapTile t);

    boolean valid(int x, int y, int zoom);

}
