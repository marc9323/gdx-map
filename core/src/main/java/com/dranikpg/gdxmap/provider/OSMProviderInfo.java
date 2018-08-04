package com.dranikpg.gdxmap.provider;

import com.dranikpg.gdxmap.opt.POT;
import com.dranikpg.gdxmap.abstr.TileProviderInfo;
import com.dranikpg.gdxmap.macro.MapTile;

public class OSMProviderInfo implements TileProviderInfo {
    @Override
    public int tileSize() {
        return 256  ;
    }

    @Override
    public int gridsize(int level) {
        return (int) POT.of(level);
    }

    @Override
    public int maxlevel() {
        return 19;
    }

    @Override
    public String getUrl(MapTile t) {
        return "http://a.tile.openstreetmap.org/"+t.level+"/"+t.x+"/"+t.y+".png";
    }

    @Override
    public boolean valid(int x, int y, int zoom) {
        if(zoom > maxlevel())return false;
        if(x<0||y<0)return false;
        int max = gridsize(zoom) ;
        return x<max&&y<max;
    }
}
