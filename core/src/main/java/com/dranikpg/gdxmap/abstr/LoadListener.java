package com.dranikpg.gdxmap.abstr;

import com.dranikpg.gdxmap.macro.MapTile;

public interface LoadListener {

    void loaded(byte[] ar, MapTile t, FetchType type);

}
