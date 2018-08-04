package com.dranikpg.gdxmap.abstr;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dranikpg.gdxmap.MapHolder;
import com.dranikpg.gdxmap.macro.MapTile;

public interface TileProvider {

    int tilesize();

    int gridsize(int level);

    int maxlevel();

    void register(MapHolder hd);

    void remove(MapHolder hd);

    boolean valid(int level, int x, int y);

    MapTile get(long code);

    MapTile get(int level, int x, int y);

    void load(long code, int load, int loadp);

    void load(int level, int x, int y, int load, int loadp);

    TextureRegion find(int level, int x, int y, int searchdepth, int load);
    TextureRegion find(int level, int x, int y, int xp, int yp, int steps, int depth);

}
