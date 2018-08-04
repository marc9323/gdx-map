package com.dranikpg.gdxmap.abstr;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface TileRenderer {

    int sizehint(int a);

    void begin(OrthographicCamera cam);

    void render(TextureRegion t, float x, float y, int ts);

    void end();

}
